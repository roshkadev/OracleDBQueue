package com.roshka.oracledbqueue.datasource;

import com.roshka.oracledbqueue.config.OracleDataSourceConfig;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

public class OracleDataSourceUtil {

    public static DataSource createPooledDataSource(OracleDataSourceConfig config)
        throws SQLException
    {

        PoolDataSource pds = PoolDataSourceFactory.getPoolDataSource();
        pds.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");

        // connection credentiales
        pds.setURL(config.getJdbcURL());
        pds.setUser(config.getJdbcUser());
        pds.setPassword(config.getJdbcPassword());

        // connection pool setup
        pds.setInitialPoolSize(config.getInitialPoolSize());
        pds.setMaxPoolSize(config.getMaxPoolSize());
        pds.setValidateConnectionOnBorrow(config.isValidateConnectionOnBorrow());
        pds.setConnectionValidationTimeout(config.getConnectionValidationTimeout());
        pds.setSQLForValidateConnection(config.getSqlForValidateConnection());
        pds.setConnectionPoolName(config.getConnectionPoolName());
        pds.setAbandonedConnectionTimeout(config.getAbandonedConnectionTimeout());
        pds.setInactiveConnectionTimeout(config.getInactiveConnectionTimeout());
        pds.setQueryTimeout(config.getQueryTimeout());
        pds.setLoginTimeout(config.getLoginTimeout());
        pds.setConnectionWaitTimeout(config.getConnectionWaitTimeout());

        return pds;

    }


}
