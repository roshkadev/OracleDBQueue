package com.roshka.oracledbqueue.db;

import com.roshka.oracledbqueue.config.OracleDBQueueConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.RowId;
import java.sql.SQLException;

public class DBCommonOperations {


    public static int updateTaskStatus(OracleDBQueueConfig config, Connection conn, RowId rowid, String oldStatus, String newStatus)
            throws SQLException
    {
        String sqlUpdate = String.format(
                "update %s set %s = ? where rowid = ? and %s = ?",
                config.getTableName(),
                config.getStatusField(),
                config.getStatusField()
        );

        PreparedStatement ps = conn.prepareStatement(sqlUpdate);
        ps.setString(1, newStatus);
        ps.setRowId(2, rowid);
        ps.setString(3, oldStatus);
        int ret = ps.executeUpdate();
        ps.close();
        return ret;
    }

}
