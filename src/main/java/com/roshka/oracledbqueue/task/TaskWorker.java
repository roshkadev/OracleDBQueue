package com.roshka.oracledbqueue.task;

import com.roshka.oracledbqueue.OracleDBQueue;
import com.roshka.oracledbqueue.OracleDBQueueCtx;
import com.roshka.oracledbqueue.config.OracleDBQueueConfig;
import com.roshka.oracledbqueue.db.DBCommonOperations;
import com.roshka.oracledbqueue.exception.OracleDBQueueException;
import com.roshka.oracledbqueue.util.OracleDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

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

        LocalDateTime t0 = LocalDateTime.now();
        taskData.setT0(t0);

        logger.info("Going to run: " + taskData.toString());

        DataSource dataSource = ctx.getDataSource();
        final OracleDBQueue oracleDBQueue = ctx.getOracleDBQueue();
        final OracleDBQueueConfig config = ctx.getConfig();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            logger.info("Attempting to get connection");
            LocalDateTime gct0 = LocalDateTime.now();
            connection = dataSource.getConnection();
            LocalDateTime gct1 = LocalDateTime.now();
            logger.info("Got connection in (msecs): " + gct0.until(gct1, ChronoUnit.MILLIS));
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);

            // check if status hasn't changed before runnning the task
            String sql = String.format("select %s from %s where rowid = ? for update nowait", config.getStatusField(), config.getTableName());
            ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            ps.setRowId(1, taskData.getRowid());
            rs = ps.executeQuery();
            if (rs.next()) {
                String currentStatus = rs.getString(config.getStatusField());
                if (currentStatus != null && currentStatus.equals(config.getStatusValInit())) {

                    logger.info("Flagging the task as QUEUED");
                    int updated = DBCommonOperations.updateTaskStatus(config, connection, taskData.getRowid(), config.getStatusValQueued());

                    logger.info("Updated task status to: " + config.getStatusValQueued() + " -> " + updated);

                    logger.info("Attempting to process task");
                    final TaskResult taskResult = oracleDBQueue.processTask(connection, taskData);
                    taskResult.setT01(LocalDateTime.now());
                    long millis = taskResult.getT00().until(taskResult.getT01(), ChronoUnit.MILLIS);
                    logger.info(String.format("Completed task execution in %f seconds", ((float)millis)/1000.0));

                    // updating task status
                    logger.info("Going to update status to " + taskResult.getNewStatus());
                    updated = DBCommonOperations.updateTaskStatus(config, connection, taskData.getRowid(), taskResult.getNewStatus());

                    logger.info("Updated task status to: " + taskResult.getNewStatus() + " -> " + updated);

                } else {
                    logger.info("Not going to process " + taskData.getRowid() + " - status is: " + currentStatus);
                }

            } else {
                logger.info("Not going to process " + taskData.getRowid() + " - Could not find row");
            }

        } catch (SQLException e) {

            logger.error("Can't run task. SQLException: " + e.getMessage(), e);
            try {
                if (connection != null) {
                    logger.info("ROLLING BACK TRANSACTION NOW");
                    connection.rollback();
                    ctx.getOracleDBQueue().reRunProcessingTaskImmediately();
                }
            } catch (SQLException throwables) {
                logger.error("Can't rollback", e);
            }

        } catch (OracleDBQueueException e) {

            logger.error("Can't run task. OracleDBQueueException", e);

            if (connection != null) {

                try {
                    DBCommonOperations.updateTaskStatus(config, connection,  taskData.getRowid(), config.getStatusValFailed());
                } catch (SQLException e1) {
                    logger.error("Ignoring SQLException when tried to update status to ERROR: " + e1.getMessage());
                }

            }

        } finally {
            if (connection != null) {
                try {
                    connection.commit();
                } catch (SQLException throwables) {
                    logger.error("Can't commit");
                }
            }
            OracleDBUtil.closeResultSetIgnoreException(rs);
            OracleDBUtil.closeStatementIgnoreException(ps);
            OracleDBUtil.closeConnectionIgnoreException(connection);
        }

        taskData.setT1(LocalDateTime.now());
        long millis = taskData.getT0().until(taskData.getT1(), ChronoUnit.MILLIS);
        long millisSinceQueued = taskData.getTq().until(taskData.getT1(), ChronoUnit.MILLIS);

        logger.debug(
                String.format(
                        "TaskData Started: %s - Ended: %s - Total Time (msecs) %d - Total Time Sinces queued (msecs) %d", taskData.getT0(), taskData.getT1(), millis, millisSinceQueued
                )
        );

    }

}
