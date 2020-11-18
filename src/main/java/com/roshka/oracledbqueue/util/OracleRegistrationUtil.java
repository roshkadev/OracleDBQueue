package com.roshka.oracledbqueue.util;

import com.roshka.oracledbqueue.listener.OracleDBQueueListener;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.dcn.DatabaseChangeListener;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class OracleRegistrationUtil {

    private static Logger logger = LoggerFactory.getLogger(OracleRegistrationUtil.class);

    public static void unregisterPreviousNotificationsForUser(OracleConnection conn, String tableName)
        throws SQLException
    {
        Statement stmt= conn.createStatement();
        ResultSet rs = stmt.executeQuery("select regid,callback,table_name from USER_CHANGE_NOTIFICATION_REGS");
        while(rs.next())
        {
            long regid = rs.getLong(1);
            String callback = rs.getString(2);
            String registrationTableName = rs.getString(3);
            if (registrationTableName != null && registrationTableName.equalsIgnoreCase(tableName)) {
                logger.info(String.format("Unregistering registration with id [%d] and callback [%s]", regid, callback));
                conn.unregisterDatabaseChangeNotification(regid,callback);
            } else {
                logger.info(String.format("Will not unregister registration with id [%d] and callback [%s] -> Registration Table Name is: ", regid, callback, registrationTableName));
            }
        }
        rs.close();
        stmt.close();
    }

    public static DatabaseChangeRegistration registerDatabaseChange(OracleConnection connection, DatabaseChangeListener listener, String tableName, String queryChange, Properties prop)
        throws SQLException
    {
        logger.debug("Registering database change notification");
        DatabaseChangeRegistration dcr = connection.registerDatabaseChangeNotification(prop);
        logger.info("Registered with id: " + dcr.getRegId());
        logger.debug("Adding listener");
        dcr.addListener(listener);

        logger.debug("Running listener query");

        // second step: add objects in the registration:
        Statement stmt = connection.createStatement();
        // associate the statement with the registration:
        ((OracleStatement)stmt).setDatabaseChangeRegistration(dcr);
        ResultSet rs = stmt.executeQuery(queryChange);
        while (rs.next())
        {}
        String[] tableNames = dcr.getTables();
        for(int i=0;i<tableNames.length;i++)
            logger.info(String.format("Table %s is included in the registration", tableNames[i]));
        rs.close();
        stmt.close();

        logger.info("Listener added successfully");

        return dcr;


    }

}
