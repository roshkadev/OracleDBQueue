package com.roshka.oracledbqueue.config;

import com.roshka.oracledbqueue.util.PropertiesUtil;
import oracle.jdbc.OracleConnection;

import java.util.Arrays;
import java.util.Properties;

public class OracleDBQueueConfig {


    public static final String CONFKEY_PREFIX = "odbq";

    private OracleDataSourceConfig dataSourceConfig;

    // listener
    public static final String CONFKEY_LISTENER_HOST = String.format("%s.listener.host", CONFKEY_PREFIX);
    private String listenerHost;

    public static final String CONFKEY_LISTENER_PORT = String.format("%s.listener.port", CONFKEY_PREFIX);
    private Integer listenerPort;

    // thread pool

    public enum ThreadPoolType {
        CACHED,
        FIXED
    }

    public static final String CONFKEY_THREAD_POOL_TYPE = String.format("%s.thread_pool.type", CONFKEY_PREFIX);
    private ThreadPoolType threadPoolType = ThreadPoolType.CACHED;


    public static final int DEFAULT_THREAD_POOL_SIZE = 16;
    public static final String CONFKEY_THREAD_POOL_SIZE = String.format("%s.thread_pool.size", CONFKEY_PREFIX);
    private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;

    // global data

    public static final String CONFKEY_TABLE_NAME = String.format("%s.table_name", CONFKEY_PREFIX);
    private String tableName;

    public static final String CONFKEY_QUERY_CHANGE_NOTIFICATIONS_DISABLED = String.format("%s.query_change_notifications_disabled", CONFKEY_PREFIX);
    private boolean queryChangeNotificationsDisabled;

    public static final String CONFKEY_STATUS_FIELD = String.format("%s.status_field", CONFKEY_PREFIX);
    private String statusField;

    public static final String DEFAULT_STATUS_VAL_INIT = "S-INIT";
    public static final String CONFKEY_STATUS_VAL_INIT = String.format("%s.status.init", CONFKEY_PREFIX);
    private String statusValInit = DEFAULT_STATUS_VAL_INIT;

    public static final String DEFAULT_STATUS_VAL_QUEUED = "S-QUEUED";
    public static final String CONFKEY_STATUS_VAL_QUEUED = String.format("%s.status.queued", CONFKEY_PREFIX);
    private String statusValQueued = DEFAULT_STATUS_VAL_QUEUED;

    public static final String DEFAULT_STATUS_VAL_FAILED = "S-FAILED";
    public static final String CONFKEY_STATUS_VAL_FAILED = String.format("%s.status.failed", CONFKEY_PREFIX);
    private String statusValFailed = DEFAULT_STATUS_VAL_FAILED;

    public static final String CONFKEY_AUXILIARY_POLL_QUEUE_INTERVAL = String.format("%s.auxiliary_poll_queue_interval", CONFKEY_PREFIX);
    public static final int DEFAULT_AUXILIARY_POLL_QUEUE_INTERVAL = 10;
    private int auxiliaryPollQueueInterval = DEFAULT_AUXILIARY_POLL_QUEUE_INTERVAL;

    public OracleDataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }

    public void setDataSourceConfig(OracleDataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }
    public static OracleDBQueueConfig loadFromProperties(Properties props) {
        OracleDBQueueConfig oracleDBQueueConfig = new OracleDBQueueConfig();

        // data structures
        oracleDBQueueConfig.setTableName(props.getProperty(CONFKEY_TABLE_NAME));
        oracleDBQueueConfig.setStatusField(props.getProperty(CONFKEY_STATUS_FIELD));
        oracleDBQueueConfig.setStatusValFailed(props.getProperty(CONFKEY_STATUS_VAL_FAILED, DEFAULT_STATUS_VAL_FAILED));
        oracleDBQueueConfig.setStatusValQueued(props.getProperty(CONFKEY_STATUS_VAL_QUEUED, DEFAULT_STATUS_VAL_QUEUED));
        oracleDBQueueConfig.setStatusValInit(props.getProperty(CONFKEY_STATUS_VAL_INIT, DEFAULT_STATUS_VAL_INIT));
        oracleDBQueueConfig.setQueryChangeNotificationsDisabled(
                Boolean.valueOf(props.getProperty(CONFKEY_QUERY_CHANGE_NOTIFICATIONS_DISABLED, "false"))
        );
        if (props.containsKey(CONFKEY_AUXILIARY_POLL_QUEUE_INTERVAL))
            oracleDBQueueConfig.setAuxiliaryPollQueueInterval(
                    PropertiesUtil.getIntegerProperty(props, CONFKEY_AUXILIARY_POLL_QUEUE_INTERVAL, DEFAULT_AUXILIARY_POLL_QUEUE_INTERVAL)
            );

        // listener

        if (props.containsKey(CONFKEY_LISTENER_HOST)) {
            oracleDBQueueConfig.setListenerHost(
                    props.getProperty(CONFKEY_LISTENER_HOST)
            );
        }
        if (props.containsKey(CONFKEY_LISTENER_PORT)) {
            oracleDBQueueConfig.setListenerPort(
                    PropertiesUtil.getIntegerProperty(props, CONFKEY_LISTENER_PORT, OracleConnection.NTF_DEFAULT_TCP_PORT)
            );
        }

        // thread pool

        try {
            oracleDBQueueConfig.setThreadPoolType(
                    ThreadPoolType.valueOf(props.getProperty(CONFKEY_THREAD_POOL_TYPE))
            );
        } catch (Throwable t) {
            oracleDBQueueConfig.setThreadPoolType(ThreadPoolType.CACHED);

        }
        oracleDBQueueConfig.setThreadPoolSize(
                PropertiesUtil.getIntegerProperty(props, CONFKEY_THREAD_POOL_SIZE, DEFAULT_THREAD_POOL_SIZE)
        );

        return oracleDBQueueConfig;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "OracleDBQueueConfig{" +
                "dataSourceConfig=" + dataSourceConfig +
                ", listenerHost='" + listenerHost + '\'' +
                ", listenerPort=" + listenerPort +
                ", threadPoolType='" + threadPoolType + '\'' +
                ", threadPoolSize=" + threadPoolSize +
                ", tableName='" + tableName + '\'' +
                ", queryChangeNotificationsDisabled=" + queryChangeNotificationsDisabled +
                ", statusField='" + statusField + '\'' +
                ", statusValInit='" + statusValInit + '\'' +
                ", statusValQueued='" + statusValQueued + '\'' +
                ", statusValFailed='" + statusValFailed + '\'' +
                ", auxiliaryPollQueueInterval=" + auxiliaryPollQueueInterval +
                '}';
    }

    public int getAuxiliaryPollQueueInterval() {
        return auxiliaryPollQueueInterval;
    }

    public void setAuxiliaryPollQueueInterval(int auxiliaryPollQueueInterval) {
        this.auxiliaryPollQueueInterval = auxiliaryPollQueueInterval;
    }

    public String getStatusField() {
        return statusField;
    }

    public void setStatusField(String statusField) {
        this.statusField = statusField;
    }

    public String getStatusValQueued() {
        return statusValQueued;
    }

    public void setStatusValQueued(String statusValQueued) {
        this.statusValQueued = statusValQueued;
    }

    public String getStatusValInit() {
        return statusValInit;
    }

    public void setStatusValInit(String statusValInit) {
        this.statusValInit = statusValInit;
    }

    public String getStatusValFailed() {
        return statusValFailed;
    }

    public void setStatusValFailed(String statusValFailed) {
        this.statusValFailed = statusValFailed;
    }

    public boolean isQueryChangeNotificationsDisabled() {
        return queryChangeNotificationsDisabled;
    }

    public void setQueryChangeNotificationsDisabled(boolean queryChangeNotificationsDisabled) {
        this.queryChangeNotificationsDisabled = queryChangeNotificationsDisabled;
    }

    public String getListenerHost() {
        return listenerHost;
    }

    public void setListenerHost(String listenerHost) {
        this.listenerHost = listenerHost;
    }

    public Integer getListenerPort() {
        return listenerPort;
    }

    public void setListenerPort(Integer listenerPort) {
        this.listenerPort = listenerPort;
    }

    public ThreadPoolType getThreadPoolType() {
        return threadPoolType;
    }

    public void setThreadPoolType(ThreadPoolType threadPoolType) {
        this.threadPoolType = threadPoolType;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }
}
