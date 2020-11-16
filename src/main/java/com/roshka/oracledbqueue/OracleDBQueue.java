package com.roshka.oracledbqueue;

import com.roshka.oracledbqueue.config.OracleDBQueueConfig;
import com.roshka.oracledbqueue.datasource.OracleDataSourceUtil;
import com.roshka.oracledbqueue.exception.ErrorConstants;
import com.roshka.oracledbqueue.exception.OracleDBQueueException;
import com.roshka.oracledbqueue.listener.OracleDBQueueListener;
import com.roshka.oracledbqueue.util.OracleDBUtil;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.datasource.OracleDataSource;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class OracleDBQueue {

    private static Logger logger = LoggerFactory.getLogger(OracleDBQueue.class);

    public enum OracleDBQueueStatus {
        CREATED,
        STARTING,
        START_FAILURE,
        RUNNING,
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

        logger.info("Setting up properties");
        Properties prop = new Properties();
        prop.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS,"true");
        prop.setProperty(OracleConnection.DCN_QUERY_CHANGE_NOTIFICATION,"true");

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

            logger.debug("Registering database change notification");
            dcr = oracleConnection.registerDatabaseChangeNotification(prop);
            logger.debug("Adding listener");
            dcr.addListener(new OracleDBQueueListener(config, dataSource));

            logger.debug("Running listener query");

            // second step: add objects in the registration:
            Statement stmt = conn.createStatement();
            // associate the statement with the registration:
            ((OracleStatement)stmt).setDatabaseChangeRegistration(dcr);
            ResultSet rs = stmt.executeQuery(config.getListenerQuery());
            while (rs.next())
            {}
            String[] tableNames = dcr.getTables();
            for(int i=0;i<tableNames.length;i++)
                logger.debug(String.format("Table %s is included in the registration", tableNames[i]));
            rs.close();
            stmt.close();

            logger.info("Listener STARTED successfully");

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
            OracleDBUtil.closeConnectionIgnoreException(oracleConnection);
        }

    }

    public void stop()
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

    }

    public OracleDBQueueConfig getConfig() {
        return config;
    }

    public OracleDBQueueStatus getStatus() {
        return status;
    }

}
