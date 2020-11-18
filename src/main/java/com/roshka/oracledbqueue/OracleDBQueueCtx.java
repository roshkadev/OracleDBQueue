package com.roshka.oracledbqueue;

import com.roshka.oracledbqueue.config.OracleDBQueueConfig;
import com.roshka.oracledbqueue.task.TaskManager;
import oracle.jdbc.dcn.DatabaseChangeRegistration;

import javax.sql.DataSource;

public class OracleDBQueueCtx {

    private OracleDBQueueConfig config;
    private OracleDBQueue oracleDBQueue;
    private TaskManager taskManager;
    private DataSource dataSource;
    private OracleDBQueue.OracleDBQueueStatus status;
    private DatabaseChangeRegistration dcr;

    OracleDBQueueCtx(OracleDBQueue oracleDBQueue)
    {
        this.oracleDBQueue = oracleDBQueue;
    }

    public OracleDBQueueConfig getConfig() {
        return config;
    }

    void setConfig(OracleDBQueueConfig config) {
        this.config = config;
    }

    public OracleDBQueue getOracleDBQueue() {
        return oracleDBQueue;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public OracleDBQueue.OracleDBQueueStatus getStatus() {
        return status;
    }

    void setStatus(OracleDBQueue.OracleDBQueueStatus status) {
        this.status = status;
    }

    public DatabaseChangeRegistration getDcr() {
        return dcr;
    }

    void setDcr(DatabaseChangeRegistration dcr) {
        this.dcr = dcr;
    }

}
