package com.roshka.oracledbqueue.task;

import com.roshka.oracledbqueue.exception.OracleDBQueueException;

import java.sql.Connection;

public interface TaskProcessor {

    /**
     *
     * @param conn
     * @param taskData
     * @return
     * @throws OracleDBQueueException
     */
    TaskResult processTask(Connection conn, TaskData taskData)
        throws OracleDBQueueException;

}
