package com.roshka.oracledbqueue.listener;

import com.roshka.oracledbqueue.config.OracleDBQueueConfig;
import oracle.jdbc.dcn.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.EnumSet;

public class OracleDBQueueListener implements DatabaseChangeListener  {
    
    private static Logger logger = LoggerFactory.getLogger(OracleDBQueueListener.class);

    private OracleDBQueueConfig config;
    private DataSource dataSource;


    public OracleDBQueueListener(OracleDBQueueConfig config, DataSource dataSource)
    {
        this.config = config;
        this.dataSource = dataSource;
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

        // this is where tasks are going to be queued
        for (QueryChangeDescription queryChangeDescription : databaseChangeEvent.getQueryChangeDescription()) {
            if (queryChangeDescription.getQueryChangeEventType() == QueryChangeDescription.QueryChangeEventType.QUERYCHANGE) {
                for (TableChangeDescription tableChangeDescription : queryChangeDescription.getTableChangeDescription()) {
                    if (tableChangeDescription.getTableOperations().contains(TableChangeDescription.TableOperation.INSERT)) {
                        logger.info(String.format("Processing [%s] operations for table [%s]", getOperationsString(tableChangeDescription.getTableOperations()), tableChangeDescription.getTableName()));
                        for (RowChangeDescription rowChangeDescription : tableChangeDescription.getRowChangeDescription()) {
                            if (rowChangeDescription.getRowOperation() == RowChangeDescription.RowOperation.INSERT) {
                                logger.info("Processing: " + rowChangeDescription.getRowid().stringValue());
                            }
                        }
                    }
                }
            }
        }


    }
}
