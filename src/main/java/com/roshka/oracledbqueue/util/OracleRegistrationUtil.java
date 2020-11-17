package com.roshka.oracledbqueue.util;

import oracle.jdbc.OracleConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
            if (registrationTableName != null && registrationTableName.equals(tableName)) {
                logger.info(String.format("Unregistering registration with id [%d] and callback [%s]", regid, callback));
                conn.unregisterDatabaseChangeNotification(regid,callback);
            } else {
                logger.info(String.format("Will not unregister registration with id [%d] and callback [%s] -> Registration Table Name is: ", regid, callback, registrationTableName));
            }
        }
        rs.close();
        stmt.close();
    }

}
