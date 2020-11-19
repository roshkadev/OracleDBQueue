package com.roshka.oracledbqueue.db;

import com.roshka.oracledbqueue.config.OracleDBQueueConfig;
import com.roshka.oracledbqueue.exception.ErrorConstants;
import com.roshka.oracledbqueue.exception.OracleDBQueueException;
import oracle.sql.ROWID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBCommonOperations {


    public static int updateTaskStatus(OracleDBQueueConfig config, Connection conn, ROWID rowid, String newStatus)
            throws SQLException
    {
        String sqlUpdate = String.format("update %s set %s = ? where rowid = ?", config.getTableName(), config.getStatusField());

        PreparedStatement ps = conn.prepareStatement(sqlUpdate);
        ps.setString(1, "QUEUED");
        ps.setRowId(2, rowid);

        int ret = ps.executeUpdate();

        ps.close();

        return ret;

    }

}
