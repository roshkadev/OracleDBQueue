package com.roshka.oracledbqueue.listener;

import com.roshka.oracledbqueue.OracleDBQueueCtx;
import com.roshka.oracledbqueue.config.OracleDBQueueConfig;
import com.roshka.oracledbqueue.exception.OracleDBQueueException;
import com.roshka.oracledbqueue.task.TaskManager;
import oracle.jdbc.dcn.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.EnumSet;

public class OracleDBQueueListener implements DatabaseChangeListener  {
    
    private static Logger logger = LoggerFactory.getLogger(OracleDBQueueListener.class);
    private OracleDBQueueCtx ctx;

    public OracleDBQueueListener(OracleDBQueueCtx ctx)
    {
        this.ctx = ctx;
    }

    private String getOperationsString(EnumSet<TableChangeDescription.TableOperation> tableOperations)
    {
        StringBuilder sb = new StringBuilder();
        for (TableChangeDescription.TableOperation tableOperation: tableOperations) {
            sb.append(tableOperation.toString()).append(",");
        }
        if (sb.length() > 0)
            sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    @Override
    public void onDatabaseChangeNotification(DatabaseChangeEvent databaseChangeEvent) {

        logger.info(databaseChangeEvent.toString());
        final OracleDBQueueConfig config = ctx.getConfig();
        final DataSource dataSource = ctx.getDataSource();
        final TaskManager taskManager = ctx.getTaskManager();

        // this is where tasks are going to be queued
        for (QueryChangeDescription queryChangeDescription : databaseChangeEvent.getQueryChangeDescription()) {

            // only take into account QUERY CHANGE events
            if (queryChangeDescription.getQueryChangeEventType() == QueryChangeDescription.QueryChangeEventType.QUERYCHANGE) {
                for (TableChangeDescription tableChangeDescription : queryChangeDescription.getTableChangeDescription()) {
                    if (tableChangeDescription.getTableName().equalsIgnoreCase(config.getTableName())) {
                        for (RowChangeDescription rowChangeDescription : tableChangeDescription.getRowChangeDescription()) {
                            if (rowChangeDescription.getRowOperation() == RowChangeDescription.RowOperation.INSERT) {
                                logger.info("Processing: " + rowChangeDescription.getRowid().stringValue());
                                try {
                                    taskManager.queueTask(rowChangeDescription.getRowid());
                                } catch (OracleDBQueueException e) {
                                    logger.error("Failure when attempting to QUEUE task: " + e.getCode() + "/" + e.getMessage());
                                }
                            }
                        }
                    }
                }
            }

        }


    }
}
