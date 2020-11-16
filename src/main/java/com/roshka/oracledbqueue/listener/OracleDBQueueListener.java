package com.roshka.oracledbqueue.listener;

import com.roshka.oracledbqueue.config.OracleDBQueueConfig;
import oracle.jdbc.dcn.DatabaseChangeEvent;
import oracle.jdbc.dcn.DatabaseChangeListener;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

public class OracleDBQueueListener implements DatabaseChangeListener  {
    
    private static Logger logger = LoggerFactory.getLogger(OracleDBQueueListener.class);

    private OracleDBQueueConfig config;
    private DataSource dataSource;


    public OracleDBQueueListener(OracleDBQueueConfig config, DataSource dataSource)
    {
        this.config = config;
        this.dataSource = dataSource;
    }

    @Override
    public void onDatabaseChangeNotification(DatabaseChangeEvent databaseChangeEvent) {



    }
}
