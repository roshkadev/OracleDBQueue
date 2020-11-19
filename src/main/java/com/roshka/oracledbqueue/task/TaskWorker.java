package com.roshka.oracledbqueue.task;

import com.roshka.oracledbqueue.OracleDBQueue;
import com.roshka.oracledbqueue.OracleDBQueueCtx;
import com.roshka.oracledbqueue.db.DBCommonOperations;
import com.roshka.oracledbqueue.exception.ErrorConstants;
import com.roshka.oracledbqueue.exception.OracleDBQueueException;
import com.roshka.oracledbqueue.util.OracleDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.BlockingQueue;

public class TaskWorker implements Runnable {
    
    private static Logger logger = LoggerFactory.getLogger(TaskWorker.class);

    private OracleDBQueueCtx ctx;
    private TaskData taskData;

    public TaskWorker(OracleDBQueueCtx ctx, TaskData taskData) {
        this.ctx = ctx;
        this.taskData = taskData;
    }

    @Override
    public void run() {

        logger.info("Going to run: " + taskData.toString());

        DataSource dataSource = ctx.getDataSource();
        final OracleDBQueue oracleDBQueue = ctx.getOracleDBQueue();
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            logger.info("Attempting to get connection");
            connection = dataSource.getConnection();
            logger.info("Got connection");

            logger.info("Attempting to process task");
            final TaskResult taskResult = oracleDBQueue.processTask(connection, taskData);
            taskResult.setT01(LocalDateTime.now());
            long millis = taskResult.getT00().until(taskResult.getT01(), ChronoUnit.MILLIS);
            logger.info(String.format("Completed task execution in %d seconds", ((float)millis)/1000.0));

            // updating task status
            logger.info("Going to update status to " + taskResult.getNewStatus());
            int updated = DBCommonOperations.updateTaskStatus(ctx.getConfig(), connection, taskData.getRowid(), taskResult.getNewStatus());

            logger.info("Updated task status to: " + taskResult.getNewStatus() + " -> " + updated);

        } catch (SQLException e) {


            logger.error("Can't run task. SQLException", e);

            if (connection != null) {

                try {
                    DBCommonOperations.updateTaskStatus(ctx.getConfig(), connection,  taskData.getRowid(), "ERROR-SQL");
                } catch (SQLException e1) {
                    logger.error("Ignoring SQLException when tried to update status to ERROR-SQL: " + e1.getMessage());
                }

            }

        } catch (OracleDBQueueException e) {

            logger.error("Can't run task. OracleDBQueueException", e);

            if (connection != null) {

                try {
                    DBCommonOperations.updateTaskStatus(ctx.getConfig(), connection,  taskData.getRowid(), "ERROR-ORADBQUEUE");
                } catch (SQLException e1) {
                    logger.error("Ignoring SQLException when tried to update status to ERROR-SQL: " + e1.getMessage());
                }

            }

        } finally {
            OracleDBUtil.closeConnectionIgnoreException(connection);
        }

    }

}
