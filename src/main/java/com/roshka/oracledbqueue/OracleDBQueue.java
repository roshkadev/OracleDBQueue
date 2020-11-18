package com.roshka.oracledbqueue;

import com.roshka.oracledbqueue.config.OracleDBQueueConfig;
import com.roshka.oracledbqueue.config.OracleDataSourceConfig;
import com.roshka.oracledbqueue.datasource.OracleDataSourceUtil;
import com.roshka.oracledbqueue.exception.ErrorConstants;
import com.roshka.oracledbqueue.exception.OracleDBQueueException;
import com.roshka.oracledbqueue.listener.OracleDBQueueListener;
import com.roshka.oracledbqueue.util.OracleDBUtil;
import com.roshka.oracledbqueue.util.OracleRegistrationUtil;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.datasource.OracleDataSource;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class OracleDBQueue implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(OracleDBQueue.class);


    @Override
    public void run() {

        try {
            logger.info("Starting Oracle's DB QUEUE");

            start();

            while(status != OracleDBQueueStatus.STOP_REQUESTED && status != OracleDBQueueStatus.ENDED) {
                Thread.sleep(20000);
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

    public enum OracleDBQueueStatus {
        CREATED,
        STARTING,
        START_FAILURE,
        RUNNING,
        STOP_REQUESTED,
        ENDED
    }

    private OracleDBQueueConfig config;
    private OracleDBQueueStatus status;

    private DataSource dataSource;
    private DatabaseChangeRegistration dcr = null;

    public OracleDBQueue(OracleDBQueueConfig config) {
        this(config, null);
    }

    public OracleDBQueue(OracleDBQueueConfig config, OracleDataSource dataSource) {
        this.config = config;
        this.dataSource = dataSource;
    }

    public void start()
            throws OracleDBQueueException
    {
        logger.info("Starting OracleDBQueue with config: " + config);
        status = OracleDBQueueStatus.STARTING;

        Connection conn = null;
        OracleConnection oracleConnection = null;


        try {
            logger.info("Analyzing oracle datasource");
            if (dataSource != null) {

            } else {
                logger.info("Setting up OracleDataSource");
                if (config.getDataSourceConfig() == null) {
                    // exception
                    status = OracleDBQueueStatus.START_FAILURE;
                    throw new OracleDBQueueException(ErrorConstants.ERR_INVALID_DATASOURCE_CONFIGURATION, "DataSource is null and no configuration was supplied");
                }
                dataSource = OracleDataSourceUtil.createPooledDataSource(config.getDataSourceConfig());
            }

            // register database change notification
            logger.debug("Getting connection!");
            conn = dataSource.getConnection();
            logger.debug("Got connection");
            oracleConnection = (OracleConnection) conn;

            // unregister previous change notifications for user
            OracleRegistrationUtil.unregisterPreviousNotificationsForUser(oracleConnection, config.getTableName());

            final OracleDBQueueListener oracleDBQueueListener = new OracleDBQueueListener(config, dataSource);

            logger.info("Setting up properties for listener");
            Properties prop = new Properties();
            prop.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS,"true");
            prop.setProperty(OracleConnection.DCN_QUERY_CHANGE_NOTIFICATION,"true");
            prop.setProperty(OracleConnection.DCN_BEST_EFFORT,"true");

            dcr = OracleRegistrationUtil.registerDatabaseChange(oracleConnection, oracleDBQueueListener, config.getTableName(), config.getListenerQuery(), prop);

            logger.info("Listener STARTED successfully with registration id: " + dcr.getRegId() + " - Setting status to RUNNING");

            status = OracleDBQueueStatus.RUNNING;

        } catch (SQLException e) {
            status = OracleDBQueueStatus.START_FAILURE;
            logger.error("Can't start OracleDBQueue", e);

            // do cleanup
            if (dcr != null) {
                try {
                    oracleConnection.unregisterDatabaseChangeNotification(dcr);
                } catch (SQLException e1) {
                    logger.error("Ignoring unregisterDatabaseNotification error: " + e.getMessage());
                }
            }

        } finally {
            logger.info("Closing connection");
            OracleDBUtil.closeConnectionIgnoreException(oracleConnection);
            logger.info("Connection closed");
        }

    }

    private void stop()
            throws OracleDBQueueException
    {

        if (dcr != null) {
            logger.info("Going to unregister database change notification: " + dcr.getRegId());
            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                final OracleConnection oracleConnection = (OracleConnection)conn;
                oracleConnection.unregisterDatabaseChangeNotification(dcr);
            } catch (SQLException e) {
                logger.error("Can't unregister database change notification", e);
            } finally {
                OracleDBUtil.closeConnectionIgnoreException(conn);
            }
        }

        status = OracleDBQueueStatus.ENDED;
    }

    public void stopRequest()
    {
        status = OracleDBQueueStatus.STOP_REQUESTED;
    }

    public OracleDBQueueConfig getConfig() {
        return config;
    }

    public OracleDBQueueStatus getStatus() {
        return status;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Hello, world");

        Properties properties = new Properties();
        properties.load(new FileReader("conf/ccp.properties"));

        final OracleDataSourceConfig oracleDataSourceConfig = OracleDataSourceConfig.loadFromProperties(properties);
        final OracleDBQueueConfig oracleDBQueueConfig = OracleDBQueueConfig.loadFromProperties(properties);
        oracleDBQueueConfig.setDataSourceConfig(oracleDataSourceConfig);

        OracleDBQueue oracleDBQueue = new OracleDBQueue(oracleDBQueueConfig);

        final Thread thread = new Thread(oracleDBQueue);
        thread.start();

        thread.join();
    }

}
