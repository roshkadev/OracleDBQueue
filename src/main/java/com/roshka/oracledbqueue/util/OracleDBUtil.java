package com.roshka.oracledbqueue.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class OracleDBUtil {

    private static Logger logger = LoggerFactory.getLogger(OracleDBUtil.class);


    public static void closeConnectionIgnoreException(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("Ignoring exception while trying to close connection: " + e.getMessage());
            }
        } else {
            logger.debug("Not attempting to close null connection.");
        }

    }

}
