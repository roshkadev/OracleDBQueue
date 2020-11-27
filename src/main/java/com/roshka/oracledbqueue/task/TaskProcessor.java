package com.roshka.oracledbqueue.task;

import com.roshka.oracledbqueue.exception.OracleDBQueueException;

import java.sql.Connection;
import java.sql.SQLException;

public interface TaskProcessor {

    /**
     *
     * @param conn
     * @param taskData
     * @return
     */
    TaskResult processTask(Connection conn, TaskData taskData);

}
