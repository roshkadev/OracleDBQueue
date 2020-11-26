package com.roshka.oracledbqueue;

import com.roshka.oracledbqueue.config.OracleDBQueueConfig;
import com.roshka.oracledbqueue.config.OracleDataSourceConfig;
import com.roshka.oracledbqueue.datasource.OracleDataSourceUtil;
import com.roshka.oracledbqueue.exception.ErrorConstants;
import com.roshka.oracledbqueue.exception.OracleDBQueueException;
import com.roshka.oracledbqueue.listener.OracleDBQueueListener;
import com.roshka.oracledbqueue.task.TaskData;
import com.roshka.oracledbqueue.task.TaskManager;
import com.roshka.oracledbqueue.task.TaskProcessor;
import com.roshka.oracledbqueue.task.TaskResult;
import com.roshka.oracledbqueue.util.OracleDBUtil;
import com.roshka.oracledbqueue.util.OracleRegistrationUtil;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.datasource.OracleDataSource;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileReader;
import java.sql.*;
import java.util.Properties;

public class OracleDBQueue implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(OracleDBQueue.class);


    public enum OracleDBQueueStatus {
        CREATED,
        STARTING,
        START_FAILURE,
        RUNNING,
        STOP_REQUESTED,
        ENDED
    }

    private OracleDBQueueCtx ctx;
    private TaskProcessor taskProcessor;

    public OracleDBQueue(OracleDBQueueConfig config) {
        this(config, null);
    }

    public OracleDBQueue(OracleDBQueueConfig config, OracleDataSource dataSource)
    {
        ctx = new OracleDBQueueCtx(this);
        ctx.setConfig(config);
        ctx.setDataSource(dataSource);
        ctx.setStatus(OracleDBQueueStatus.CREATED);
    }



    @Override
    public void run() {

        try {
            logger.info("Starting Oracle's DB QUEUE");
            final OracleDBQueueConfig config = ctx.getConfig();

            start();

            processPendingTasks();

            while(ctx.getStatus() != OracleDBQueueStatus.STOP_REQUESTED && ctx.getStatus() != OracleDBQueueStatus.ENDED) {
                Thread.sleep(config.getAuxiliaryPollQueueInterval()*1000);
                // call to run the query manually
                processPendingTasks();
            }
        } catch (InterruptedException e) {
            logger.error("Thread was interrupted, cleaning up and exiting");
        } catch (OracleDBQueueException e) {
            logger.error("OracleDBQueue could not start. Giving up...", e);
        } finally {
            try {
                logger.info("Going to STOP");
                stop();
                logger.info("STOPPED");
            } catch (OracleDBQueueException e) {
                logger.error("Failure while stopping QUEUE", e);
            }
        }
        logger.info("Bye, bye now");
    }

    private void processPendingTasks() {

        logger.info("Processing pending TASKS");

        final OracleDBQueueConfig config = ctx.getConfig();
        final DataSource dataSource = ctx.getDataSource();
        final TaskManager taskManager = ctx.getTaskManager();

        String sql = config.getListenerQuery();
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        RowId currentRowId = null;
        try {

            logger.debug("Going to run query: " + sql);
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                currentRowId = rs.getRowId("rowid");
                taskManager.processTaskDataInRS(TaskManager.TaskQueueType.QUERY_POLLING_THREAD, conn, rs);
            }

        } catch (SQLException e) {
            boolean handled = false;
            if (e.getErrorCode() == 8177) {
                handled = true;
                logger.info("Not processing rowid " + (currentRowId != null ? currentRowId.toString() : "N/A - It was probably processed in the QUERY CHANGE NOTIFICATION event"));
            }
            if (!handled)
                logger.error("Unexpected SQLException while processing pending tasks", e);
        } catch (OracleDBQueueException e) {
            logger.error("OracleDBQueueException while processing pending tasks", e);
        } finally {
            OracleDBUtil.closeResultSetIgnoreException(rs);
            OracleDBUtil.closeStatementIgnoreException(st);
            OracleDBUtil.closeConnectionIgnoreException(conn);
        }

    }

    public void start()
            throws OracleDBQueueException
    {
        final OracleDBQueueConfig config = ctx.getConfig();
        logger.info("Starting OracleDBQueue with config: " + config);
        ctx.setStatus(OracleDBQueueStatus.STARTING);

        Connection conn = null;


        try {

            // setup task manager
            setupTaskManager();

            // setup data source if needed
            setupDataSource();

            // register database change notification
            logger.debug("Getting connection!");
            conn = ctx.getDataSource().getConnection();

            if (!config.isQueryChangeNotificationsDisabled()) {
                registeringDatabaseChangeNotification((OracleConnection)conn);
            } else {
                logger.warn("QUERY CHANGE NOTIFICATIONS are disabled. It's recommended to use them for best performance");
            }

            // set status to running
            ctx.setStatus(OracleDBQueueStatus.RUNNING);

        } catch (SQLException e) {
            ctx.setStatus(OracleDBQueueStatus.START_FAILURE);
            logger.error("Can't start OracleDBQueue", e);

            // do cleanup
            if (ctx.getDcr() != null) {
                try {
                    ((OracleConnection)conn).unregisterDatabaseChangeNotification(ctx.getDcr());
                } catch (SQLException e1) {
                    logger.error("Ignoring unregisterDatabaseNotification error: " + e.getMessage());
                }
            }

        } finally {
            logger.info("Closing connection");
            OracleDBUtil.closeConnectionIgnoreException(conn);
            logger.info("Connection closed");
        }

    }

    private void setupTaskManager() {
        logger.info("Setting up task manager");

        TaskManager taskManager = new TaskManager(ctx);
        ctx.setTaskManager(taskManager);

    }

    private void setupDataSource() throws OracleDBQueueException, SQLException {
        final OracleDBQueueConfig config = ctx.getConfig();
        logger.info("Analyzing oracle datasource");
        if (ctx.getDataSource() != null) {
            // do nothing, just run
        } else {
            logger.info("Setting up OracleDataSource");
            if (config.getDataSourceConfig() == null) {
                // exception
                ctx.setStatus(OracleDBQueueStatus.START_FAILURE);
                throw new OracleDBQueueException(ErrorConstants.ERR_INVALID_DATASOURCE_CONFIGURATION, "DataSource is null and no configuration was supplied");
            }
            ctx.setDataSource(OracleDataSourceUtil.createPooledDataSource(config.getDataSourceConfig()));
        }

    }

    private void registeringDatabaseChangeNotification(OracleConnection oracleConnection) throws SQLException {

        final OracleDBQueueConfig config = ctx.getConfig();

        // unregister previous change notifications for user
        OracleRegistrationUtil.unregisterPreviousNotificationsForUser(oracleConnection, config.getTableName());

        final OracleDBQueueListener oracleDBQueueListener = new OracleDBQueueListener(ctx);

        logger.info("Setting up properties for listener");
        Properties prop = new Properties();
        prop.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS,"true");
        prop.setProperty(OracleConnection.DCN_QUERY_CHANGE_NOTIFICATION,"true");
        prop.setProperty(OracleConnection.DCN_BEST_EFFORT,"true");
        if (config.getListenerHost() != null) {
            prop.setProperty(OracleConnection.NTF_LOCAL_HOST, config.getListenerHost());
        }
        if (config.getListenerPort() != null) {
            prop.setProperty(OracleConnection.NTF_LOCAL_TCP_PORT, String.valueOf(config.getListenerPort()));
        }

        final DatabaseChangeRegistration databaseChangeRegistration = OracleRegistrationUtil.registerDatabaseChange(oracleConnection, oracleDBQueueListener, config.getTableName(), config.getListenerQuery(), prop);
        logger.info(String.format(
                    "Listener STARTED successfully with registration id [%s] Specified Host [%s] Specified Port [%s] - Setting status to RUNNING",
                    databaseChangeRegistration.getRegId(),
                    databaseChangeRegistration.getRegistrationOptions().getProperty(OracleConnection.NTF_LOCAL_HOST),
                    databaseChangeRegistration.getRegistrationOptions().getProperty(OracleConnection.NTF_LOCAL_TCP_PORT)
                )
        );
        ctx.setDcr(databaseChangeRegistration);
    }

    private void stop()
            throws OracleDBQueueException
    {
        final DatabaseChangeRegistration dcr = ctx.getDcr();

        if (dcr != null) {
            logger.info("Going to unregister database change notification: " + dcr.getRegId());
            Connection conn = null;
            try {
                conn = ctx.getDataSource().getConnection();
                final OracleConnection oracleConnection = (OracleConnection)conn;
                oracleConnection.unregisterDatabaseChangeNotification(dcr);
            } catch (SQLException e) {
                logger.error("Can't unregister database change notification", e);
            } finally {
                OracleDBUtil.closeConnectionIgnoreException(conn);
            }
        }

        ctx.setStatus(OracleDBQueueStatus.ENDED);
    }

    public void stopRequest()
    {
        ctx.setStatus(OracleDBQueueStatus.STOP_REQUESTED);
    }

    public void registerTaskProcessor(TaskProcessor taskProcessor) {
        this.taskProcessor = taskProcessor;
    }

    public TaskResult processTask(Connection conn, TaskData taskData)
        throws OracleDBQueueException
    {
        return taskProcessor.processTask(conn, taskData);
    }

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileReader("conf/ccp.properties"));

        final OracleDataSourceConfig oracleDataSourceConfig = OracleDataSourceConfig.loadFromProperties(properties);
        final OracleDBQueueConfig oracleDBQueueConfig = OracleDBQueueConfig.loadFromProperties(properties);
        oracleDBQueueConfig.setDataSourceConfig(oracleDataSourceConfig);

        OracleDBQueue oracleDBQueue = new OracleDBQueue(oracleDBQueueConfig);

        oracleDBQueue.registerTaskProcessor(new TaskProcessor() {
            @Override
            public TaskResult processTask(Connection conn, TaskData taskData) throws OracleDBQueueException {
                logger.info("Processing task data: " + taskData.toString());
                TaskResult taskResult = new TaskResult(taskData.getCurrentStatus());

                String updateSQL = "update MICHI.GE_SNP_MEN_ENVIADOS set cod_msj_swift = cod_msj_swift + 1, fec_proceso = SYSDATE, msj_resultado = ? where rowid = ?";
                try {
                    PreparedStatement ps = conn.prepareStatement(updateSQL);
                    ps.setString(1, taskData.getTaskQueueType().toString());
                    ps.setRowId(2, taskData.getRowid());
                    ps.executeUpdate();
                    taskResult.setNewStatus("OK");
                } catch (SQLException e) {
                    taskResult.setNewStatus("ERR");
                    logger.error("Error on TASK PROCESSOR", e);
                }
                return taskResult;
            }
        });


        final Thread thread = new Thread(oracleDBQueue);
        thread.start();

        thread.join();
    }


}
