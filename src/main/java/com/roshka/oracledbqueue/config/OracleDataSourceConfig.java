package com.roshka.oracledbqueue.config;

import com.roshka.oracledbqueue.datasource.OracleDataSourceUtil;
import com.roshka.oracledbqueue.util.PropertiesUtil;

import java.util.Properties;

public class OracleDataSourceConfig implements Cloneable {

    public static final String CONFKEY_PREFIX = "odbq.ds";

    // Connection Credentials
    public static final String CONFKEY_JDBC_URL = String.format("%s.jdbc_url", CONFKEY_PREFIX);
    private String jdbcURL;
    public static final String CONFKEY_JDBC_USER = String.format("%s.jdbc_user", CONFKEY_PREFIX);
    private String jdbcUser;
    public static final String CONFKEY_JDBC_PASSWORD = String.format("%s.jdbc_password", CONFKEY_PREFIX);
    private String jdbcPassword;

    // Connection Pool
    public static final String CONFKEY_INITIAL_POOL_SIZE = String.format("%s.initial_pool_size", CONFKEY_PREFIX);
    public static final int DEFAULT_INITIAL_POOL_SIZE = 3;
    private int initialPoolSize = DEFAULT_INITIAL_POOL_SIZE;

    public static final String CONFKEY_MAX_POOL_SIZE = String.format("%s.max_pool_size", CONFKEY_PREFIX);
    public static final int DEFAULT_MAX_POOL_SIZE = 10;
    private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;

    public static final String CONFKEY_VALIDATE_CONNECTION_ON_BORROW = String.format("%s.validate_connection_on_borrow", CONFKEY_PREFIX);
    public static final boolean DEFAULT_VALIDATE_CONNECTION_ON_BORROW = true;
    private boolean validateConnectionOnBorrow = DEFAULT_VALIDATE_CONNECTION_ON_BORROW;

    public static final String CONFKEY_CONNECTION_VALIDATION_TIMEOUT = String.format("%s.connection_validation_timeout", CONFKEY_PREFIX);
    public static final Integer DEFAULT_CONNECTION_VALIDATION_TIMEOUT = null;
    private Integer connectionValidationTimeout = DEFAULT_CONNECTION_VALIDATION_TIMEOUT;

    public static final String CONFKEY_SQL_FOR_VALIDATE_CONNECTION = String.format("%s.sql_for_validate_connection", CONFKEY_PREFIX);
    public static final String DEFAULT_SQL_FOR_VALIDATE_CONNECTION = "select 1 from dual";
    private String sqlForValidateConnection = DEFAULT_SQL_FOR_VALIDATE_CONNECTION;

    public static final String CONFKEY_CONNECTION_POOL_NAME = String.format("%s.connection_pool_name", CONFKEY_PREFIX);
    public static final String DEFAULT_CONNECTION_POOL_NAME = "OracleDBQueuePool";
    private String connectionPoolName = DEFAULT_CONNECTION_POOL_NAME;

    public static final String CONFKEY_ABANDONED_CONNECTION_TIMEOUT = String.format("%s.abandoned_connection_timeout", CONFKEY_PREFIX);
    public static final Integer DEFAULT_ABANDONED_CONNECTION_TIMEOUT = null;
    private Integer abandonedConnectionTimeout = DEFAULT_ABANDONED_CONNECTION_TIMEOUT;

    public static final String CONFKEY_INACTIVE_CONNECTION_TIMEOUT = String.format("%s.inactive_connection_timeout", CONFKEY_PREFIX);
    public static final Integer DEFAULT_INACTIVE_CONNECTION_TIMEOUT = null;
    private Integer inactiveConnectionTimeout = DEFAULT_INACTIVE_CONNECTION_TIMEOUT;

    public static final String CONFKEY_QUERY_TIMEOUT = String.format("%s.query_timeout", CONFKEY_PREFIX);
    public static final Integer DEFAULT_QUERY_TIMEOUT = null;
    private Integer queryTimeout = DEFAULT_QUERY_TIMEOUT;

    public static final String CONFKEY_LOGIN_TIMEOUT = String.format("%s.login_timeout", CONFKEY_PREFIX);
    public static final Integer DEFAULT_LOGIN_TIMEOUT = null;
    private Integer loginTimeout = DEFAULT_LOGIN_TIMEOUT;

    public static final String CONFKEY_CONNECTION_WAIT_TIMEOUT = String.format("%s.connection_wait_timeout", CONFKEY_PREFIX);
    public static final Integer DEFAULT_CONNECTION_WAIT_TIMEOUT = null;
    private Integer connectionWaitTimeout = DEFAULT_CONNECTION_WAIT_TIMEOUT;

    public OracleDataSourceConfig()
    {
    }

    public OracleDataSourceConfig(OracleDataSourceConfig dataSourceConfig) {
        this.setJdbcPassword(dataSourceConfig.getJdbcPassword());
        this.setJdbcUser(dataSourceConfig.getJdbcUser());
        this.setJdbcURL(dataSourceConfig.getJdbcURL());
        this.setAbandonedConnectionTimeout(dataSourceConfig.getAbandonedConnectionTimeout());
        this.setConnectionPoolName(dataSourceConfig.getConnectionPoolName());
        this.setConnectionValidationTimeout(dataSourceConfig.getConnectionValidationTimeout());
        this.setConnectionWaitTimeout(dataSourceConfig.getConnectionWaitTimeout());
        this.setInactiveConnectionTimeout(dataSourceConfig.getInactiveConnectionTimeout());
        this.setInitialPoolSize(dataSourceConfig.getInitialPoolSize());
        this.setLoginTimeout(dataSourceConfig.getLoginTimeout());
        this.setMaxPoolSize(dataSourceConfig.getMaxPoolSize());
        this.setQueryTimeout(dataSourceConfig.getQueryTimeout());
        this.setSqlForValidateConnection(dataSourceConfig.getSqlForValidateConnection());
        this.setValidateConnectionOnBorrow(dataSourceConfig.isValidateConnectionOnBorrow());
    }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public void setJdbcUser(String jdbcUser) {
        this.jdbcUser = jdbcUser;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public boolean isValidateConnectionOnBorrow() {
        return validateConnectionOnBorrow;
    }

    public void setValidateConnectionOnBorrow(boolean validateConnectionOnBorrow) {
        this.validateConnectionOnBorrow = validateConnectionOnBorrow;
    }

    public Integer getConnectionValidationTimeout() {
        return connectionValidationTimeout;
    }

    public void setConnectionValidationTimeout(Integer connectionValidationTimeout) {
        this.connectionValidationTimeout = connectionValidationTimeout;
    }

    public String getSqlForValidateConnection() {
        return sqlForValidateConnection;
    }

    public void setSqlForValidateConnection(String sqlForValidateConnection) {
        this.sqlForValidateConnection = sqlForValidateConnection;
    }

    public String getConnectionPoolName() {
        return connectionPoolName;
    }

    public void setConnectionPoolName(String connectionPoolName) {
        this.connectionPoolName = connectionPoolName;
    }

    public Integer getAbandonedConnectionTimeout() {
        return abandonedConnectionTimeout;
    }

    public void setAbandonedConnectionTimeout(Integer abandonedConnectionTimeout) {
        this.abandonedConnectionTimeout = abandonedConnectionTimeout;
    }

    public Integer getInactiveConnectionTimeout() {
        return inactiveConnectionTimeout;
    }

    public void setInactiveConnectionTimeout(Integer inactiveConnectionTimeout) {
        this.inactiveConnectionTimeout = inactiveConnectionTimeout;
    }

    public Integer getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(Integer queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public Integer getLoginTimeout() {
        return loginTimeout;
    }

    public void setLoginTimeout(Integer loginTimeout) {
        this.loginTimeout = loginTimeout;
    }

    public Integer getConnectionWaitTimeout() {
        return connectionWaitTimeout;
    }

    public void setConnectionWaitTimeout(Integer connectionWaitTimeout) {
        this.connectionWaitTimeout = connectionWaitTimeout;
    }

    public static OracleDataSourceConfig loadFromProperties(Properties props)
    {
        OracleDataSourceConfig oracleDataSourceConfig = new OracleDataSourceConfig();

        oracleDataSourceConfig.setJdbcURL(props.getProperty(CONFKEY_JDBC_URL));
        oracleDataSourceConfig.setJdbcUser(props.getProperty(CONFKEY_JDBC_USER));
        oracleDataSourceConfig.setJdbcPassword(props.getProperty(CONFKEY_JDBC_PASSWORD));

        if (props.containsKey(CONFKEY_INITIAL_POOL_SIZE)) {
            oracleDataSourceConfig.setInitialPoolSize(PropertiesUtil.getIntegerProperty(props, CONFKEY_INITIAL_POOL_SIZE));
        }

        if (props.containsKey(CONFKEY_MAX_POOL_SIZE)) {
            oracleDataSourceConfig.setMaxPoolSize(PropertiesUtil.getIntegerProperty(props, CONFKEY_MAX_POOL_SIZE));
        }

        oracleDataSourceConfig.setValidateConnectionOnBorrow(Boolean.valueOf(props.getProperty(CONFKEY_VALIDATE_CONNECTION_ON_BORROW, "true")));

        if (props.containsKey(CONFKEY_CONNECTION_VALIDATION_TIMEOUT)) {
            oracleDataSourceConfig.setConnectionValidationTimeout(PropertiesUtil.getIntegerProperty(props, CONFKEY_CONNECTION_VALIDATION_TIMEOUT));
        }

        oracleDataSourceConfig.setSqlForValidateConnection(props.getProperty(CONFKEY_SQL_FOR_VALIDATE_CONNECTION, DEFAULT_SQL_FOR_VALIDATE_CONNECTION));


        if (props.containsKey(CONFKEY_CONNECTION_POOL_NAME)) {
            oracleDataSourceConfig.setConnectionPoolName(props.getProperty(CONFKEY_CONNECTION_POOL_NAME));
        }

        if (props.containsKey(CONFKEY_ABANDONED_CONNECTION_TIMEOUT)) {
            oracleDataSourceConfig.setAbandonedConnectionTimeout(PropertiesUtil.getIntegerProperty(props, CONFKEY_ABANDONED_CONNECTION_TIMEOUT));
        }

        if (props.containsKey(CONFKEY_INACTIVE_CONNECTION_TIMEOUT)) {
            oracleDataSourceConfig.setInactiveConnectionTimeout(PropertiesUtil.getIntegerProperty(props, CONFKEY_INACTIVE_CONNECTION_TIMEOUT));
        }

        if (props.containsKey(CONFKEY_QUERY_TIMEOUT)) {
            oracleDataSourceConfig.setQueryTimeout(PropertiesUtil.getIntegerProperty(props, CONFKEY_QUERY_TIMEOUT));
        }

        if (props.containsKey(CONFKEY_LOGIN_TIMEOUT)) {
            oracleDataSourceConfig.setLoginTimeout(PropertiesUtil.getIntegerProperty(props, CONFKEY_LOGIN_TIMEOUT));
        }

        if (props.containsKey(CONFKEY_CONNECTION_WAIT_TIMEOUT)) {
            oracleDataSourceConfig.setConnectionWaitTimeout(PropertiesUtil.getIntegerProperty(props, CONFKEY_CONNECTION_WAIT_TIMEOUT));
        }

        return oracleDataSourceConfig;
    }

    @Override
    public String toString() {
        return "OracleDataSourceConfig{" +
                "jdbcURL='" + jdbcURL + '\'' +
                ", jdbcUser='" + jdbcUser + '\'' +
                ", jdbcPassword='" + "****" + '\'' +
                ", initialPoolSize=" + initialPoolSize +
                ", maxPoolSize=" + maxPoolSize +
                ", validateConnectionOnBorrow=" + validateConnectionOnBorrow +
                ", connectionValidationTimeout=" + connectionValidationTimeout +
                ", sqlForValidateConnection='" + sqlForValidateConnection + '\'' +
                ", connectionPoolName='" + connectionPoolName + '\'' +
                ", abandonedConnectionTimeout=" + abandonedConnectionTimeout +
                ", inactiveConnectionTimeout=" + inactiveConnectionTimeout +
                ", queryTimeout=" + queryTimeout +
                ", loginTimeout=" + loginTimeout +
                ", connectionWaitTimeout=" + connectionWaitTimeout +
                '}';
    }
}
