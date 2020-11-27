package com.roshka.oracledbqueue.task;

import com.roshka.oracledbqueue.OracleDBQueueCtx;
import com.roshka.oracledbqueue.config.OracleDBQueueConfig;
import com.roshka.oracledbqueue.exception.OracleDBQueueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TaskManager {

    public enum TaskQueueType {
        QUERY_CHANGE_NOTIFICATION_EVENT,
        QUERY_POLLING_THREAD
    }

    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    private OracleDBQueueCtx ctx;

    private ThreadPoolExecutor executor;



    public TaskManager(OracleDBQueueCtx ctx) {
        this.ctx = ctx;
        setupExecutor();
    }

    private void setupExecutor() {
        final OracleDBQueueConfig config = this.ctx.getConfig();

        if (config.getThreadPoolType() == OracleDBQueueConfig.ThreadPoolType.CACHED) {
            logger.info("Using CACHED thread pool executor");
            executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        } else if (config.getThreadPoolType() == OracleDBQueueConfig.ThreadPoolType.FIXED) {
            logger.info("Using FIXED thread pool executor with " + config.getThreadPoolSize() + " size");
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getThreadPoolSize());
        } else {
            logger.error("Unhandled executor type found, using default: " + config.getThreadPoolType());
            executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        }

    }

    public void stop()
    {
        executor.shutdown();
    }

    public TaskData createTaskDataFromRS(TaskQueueType taskQueueType, ResultSet rs, RowId rowid)
        throws SQLException
    {
        TaskData taskData = new TaskData(taskQueueType);
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
     * @throws OracleDBQueueException
     */
    public void queueTask(TaskData taskData)
            throws OracleDBQueueException
    {
        processTaskDataInRS(taskData);
    }

    public void processTaskDataInRS(TaskData taskData) {
        taskData.setTq(LocalDateTime.now());
        executor.execute(new TaskWorker(ctx, taskData));
    }

}
