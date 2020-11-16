package com.roshka.oracledbqueue.config;

public class OracleDataSourceConfig {

    // Connection Credentials
    private String jdbcURL;
    private String jdbcUser;
    private String jdbcPassword;

    // Connection Pool
    public static final int DEFAULT_INITIAL_POOL_SIZE = 3;
    private int initialPoolSize = DEFAULT_INITIAL_POOL_SIZE;
    public static final int DEFAULT_MAX_POOL_SIZE = 10;
    private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
    public static final boolean DEFAULT_VALIDATE_CONNECTION_ON_BORROW = true;
    private boolean validateConnectionOnBorrow = DEFAULT_VALIDATE_CONNECTION_ON_BORROW;
    public static final int DEFAULT_CONNECTION_VALIDATION_TIMEOUT = 10;
    private int connectionValidationTimeout = DEFAULT_CONNECTION_VALIDATION_TIMEOUT;
    public static final String DEFAULT_SQL_FOR_VALIDATE_CONNECTION = "select 1 from dual";
    private String sqlForValidateConnection = DEFAULT_SQL_FOR_VALIDATE_CONNECTION;
    public static final String DEFAULT_CONNECTION_POOL_NAME = "OracleDBQueuePool";
    private String connectionPoolName = DEFAULT_CONNECTION_POOL_NAME;
    public static final int DEFAULT_ABANDONED_CONNECTION_TIMEOUT = 10;
    private int abandonedConnectionTimeout = DEFAULT_ABANDONED_CONNECTION_TIMEOUT;
    public static final int DEFAULT_INACTIVE_CONNECTION_TIMEOUT = 10;
    private int inactiveConnectionTimeout = DEFAULT_INACTIVE_CONNECTION_TIMEOUT;
    public static final int DEFAULT_QUERY_TIMEOUT = 10;
    private int queryTimeout = DEFAULT_QUERY_TIMEOUT;
    public static final int DEFAULT_LOGIN_TIMEOUT = 10;
    private int loginTimeout = DEFAULT_LOGIN_TIMEOUT;
    public static final int DEFAULT_CONNECTION_WAIT_TIMEOUT = 10;
    private int connectionWaitTimeout = DEFAULT_CONNECTION_WAIT_TIMEOUT;

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

    public int getConnectionValidationTimeout() {
        return connectionValidationTimeout;
    }

    public void setConnectionValidationTimeout(int connectionValidationTimeout) {
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

    public int getAbandonedConnectionTimeout() {
        return abandonedConnectionTimeout;
    }

    public void setAbandonedConnectionTimeout(int abandonedConnectionTimeout) {
        this.abandonedConnectionTimeout = abandonedConnectionTimeout;
    }

    public int getInactiveConnectionTimeout() {
        return inactiveConnectionTimeout;
    }

    public void setInactiveConnectionTimeout(int inactiveConnectionTimeout) {
        this.inactiveConnectionTimeout = inactiveConnectionTimeout;
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public int getLoginTimeout() {
        return loginTimeout;
    }

    public void setLoginTimeout(int loginTimeout) {
        this.loginTimeout = loginTimeout;
    }

    public int getConnectionWaitTimeout() {
        return connectionWaitTimeout;
    }

    public void setConnectionWaitTimeout(int connectionWaitTimeout) {
        this.connectionWaitTimeout = connectionWaitTimeout;
    }
}
