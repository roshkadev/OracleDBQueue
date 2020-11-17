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

        // connection credentials
        pds.setURL(config.getJdbcURL());
        pds.setUser(config.getJdbcUser());
        pds.setPassword(config.getJdbcPassword());

        // connection pool setup
        pds.setInitialPoolSize(config.getInitialPoolSize());
        pds.setMaxPoolSize(config.getMaxPoolSize());
        pds.setValidateConnectionOnBorrow(config.isValidateConnectionOnBorrow());

        if (config.getConnectionValidationTimeout() != null)
            pds.setConnectionValidationTimeout(config.getConnectionValidationTimeout());

        pds.setSQLForValidateConnection(config.getSqlForValidateConnection());

        if (config.getConnectionPoolName() != null)
            pds.setConnectionPoolName(config.getConnectionPoolName());


        if (config.getAbandonedConnectionTimeout() != null)
            pds.setAbandonedConnectionTimeout(config.getAbandonedConnectionTimeout());

        if (config.getInactiveConnectionTimeout() != null)
            pds.setInactiveConnectionTimeout(config.getInactiveConnectionTimeout());

        if (config.getQueryTimeout() != null)
            pds.setQueryTimeout(config.getQueryTimeout());

        if (config.getLoginTimeout() != null)
            pds.setLoginTimeout(config.getLoginTimeout());

        if (config.getConnectionWaitTimeout() != null)
            pds.setConnectionWaitTimeout(config.getConnectionWaitTimeout());

        return pds;

    }


}
