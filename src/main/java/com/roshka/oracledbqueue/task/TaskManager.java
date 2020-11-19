package com.roshka.oracledbqueue.task;

import com.roshka.oracledbqueue.config.OracleDBQueueConfig;
import com.roshka.oracledbqueue.OracleDBQueueCtx;
import com.roshka.oracledbqueue.db.DBCommonOperations;
import com.roshka.oracledbqueue.exception.ErrorConstants;
import com.roshka.oracledbqueue.exception.OracleDBQueueException;
import com.roshka.oracledbqueue.util.OracleDBUtil;
import oracle.sql.ROWID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class TaskManager {


    private static Logger logger = LoggerFactory.getLogger(TaskManager.class);

    private OracleDBQueueCtx ctx;

    private ThreadPoolExecutor executor;



    public TaskManager(OracleDBQueueCtx ctx) {
        this.ctx = ctx;
    }

    public void init()
    {

        setupExecutor();

    }

    private void setupExecutor() {

        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    }

    private TaskData createTaskDataFromRS(ResultSet rs, ROWID rowid)
        throws SQLException
    {
        TaskData taskData = new TaskData();
        taskData.setRowid(rowid);


        Map<String, Object> data = new HashMap<>();
        taskData.setData(data);

        ResultSetMetaData rsmd = rs.getMetaData();

        for (int i=0; i<rsmd.getColumnCount(); i++) {
            final String columnName = rsmd.getColumnName(i+1);
            if (columnName.equalsIgnoreCase(ctx.getConfig().getStatusField())) {
                taskData.setCurrentStatus(rs.getString(i+1));
            }
            data.put(columnName, rs.getObject(i+1));
        }

        return taskData;
    }

    /**
     * This function just gets the data from the table, and qu
     * @param rowid
     * @throws OracleDBQueueException
     */
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
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String sqlGet = String.format("select xxx.* from %s xxx where rowid = ?", config.getTableName());
            ps = conn.prepareStatement(sqlGet, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ps.setRowId(1, rowid);
            rs = ps.executeQuery();
            if (rs.next()) {
                TaskData taskData = createTaskDataFromRS(rs, rowid);
                logger.info("GOT TASK DATA!");
                logger.info(taskData.toString());
                rs.close();
                ps.close();

                int updated = DBCommonOperations.updateTaskStatus(config, conn, rowid, "QUEUED");

                if (updated != 1) {
                    throw new OracleDBQueueException(
                            ErrorConstants.ERR_CANT_UPDATE_TASK,
                            String.format("Row with ROWID [" + rowid.stringValue() + "] " +
                                            "status was not found for update. Table: [%s] Field [%s] ROWID [%s]" +
                                            config.getTableName(),
                                    config.getStatusField(),
                                    rowid.stringValue()
                            )
                    );
                }

                // queueing task into a separate thread
                executor.execute(new TaskWorker(ctx, taskData));

            } else {
                throw new OracleDBQueueException(ErrorConstants.ERR_ROW_NOT_FOUND, "Row with ROWID [" + rowid.stringValue() + "] not found on table " + config.getTableName());
            }
            conn.commit();

        } catch (SQLException e) {
            try {
                logger.info("Rolling back transaction");
                conn.rollback();
                logger.info("Rollback completed");
            } catch (SQLException e1) {
                logger.error("Cant' rollback", e1);
            }
            throw new OracleDBQueueException(ErrorConstants.SQL_ERROR, "SQLException while queueing task: " + e.getMessage(), e);
        } finally {
            OracleDBUtil.closeResultSetIgnoreException(rs);
            OracleDBUtil.closeStatementIgnoreException(ps);
            OracleDBUtil.closeConnectionIgnoreException(conn);
        }

    }
    
}
