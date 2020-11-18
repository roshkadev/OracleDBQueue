package com.roshka.oracledbqueue.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public static void closeStatementIgnoreException(Statement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                logger.error("Ignoring exception while trying to close statement: " + e.getMessage());
            }
        } else {
            logger.debug("Not attempting to close null statement.");
        }
    }

    public static void closeResultSetIgnoreException(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("Ignoring exception while trying to close result set: " + e.getMessage());
            }
        } else {
            logger.debug("Not attempting to close null result set.");
        }
    }
}
