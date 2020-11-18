package com.roshka.oracledbqueue.task;

import com.roshka.oracledbqueue.config.OracleDBQueueConfig;
import com.roshka.oracledbqueue.OracleDBQueueCtx;
import com.roshka.oracledbqueue.exception.ErrorConstants;
import com.roshka.oracledbqueue.exception.OracleDBQueueException;
import com.roshka.oracledbqueue.util.OracleDBUtil;
import oracle.sql.ROWID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskManager {


    private static Logger logger = LoggerFactory.getLogger(TaskManager.class);

    private OracleDBQueueCtx ctx;

    public TaskManager(OracleDBQueueCtx ctx) {
        this.ctx = ctx;
    }

    public void init()
    {

    }

    public void queueTask(ROWID rowid)
            throws OracleDBQueueException
    {
        final DataSource dataSource = ctx.getDataSource();
        final OracleDBQueueConfig config = ctx.getConfig();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            String sql = String.format("select * from %s where rowid = ?", config.getTableName());
            ps = conn.prepareCall(sql);
            ps.setRowId(1, rowid);
            rs = ps.executeQuery();
            if (rs.next()) {



            } else {
                throw new OracleDBQueueException(ErrorConstants.ERR_ROW_NOT_FOUND, "Row with ROWID [" + rowid.stringValue() + "] not found on table " + config.getTableName());
            }


        } catch (SQLException e) {

        } finally {
            OracleDBUtil.closeResultSetIgnoreException(rs);
            OracleDBUtil.closeStatementIgnoreException(ps);
            OracleDBUtil.closeConnectionIgnoreException(conn);
        }


    }
    
}
