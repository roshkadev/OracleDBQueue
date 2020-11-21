package com.roshka.oracledbqueue.config;

import com.roshka.oracledbqueue.util.PropertiesUtil;

import java.util.Properties;

public class OracleDBQueueConfig {

    public static final String CONFKEY_PREFIX = "odbq";

    private OracleDataSourceConfig dataSourceConfig;

    public static final String CONFKEY_LISTENER_QUERY = String.format("%s.listener_query", CONFKEY_PREFIX);
    private String listenerQuery;

    public static final String CONFKEY_TABLE_NAME = String.format("%s.table_name", CONFKEY_PREFIX);
    private String tableName;

    public static final String CONFKEY_STATUS_FIELD = String.format("%s.status_field", CONFKEY_PREFIX);
    private String statusField;

    public static final String CONFKEY_STATUS_VAL_QUEUED = String.format("%s.status.queued", CONFKEY_PREFIX);
    private String statusValQueued;

    public static final String CONFKEY_STATUS_VAL_FAILED = String.format("%s.status.failed", CONFKEY_PREFIX);
    private String statusValFailed;

    public static final String CONFKEY_AUXILIARY_POLL_QUEUE_INTERVAL = String.format("%s.auxiliary_poll_queue_interval", CONFKEY_PREFIX);
    public static final int DEFAULT_AUXILIARY_POLL_QUEUE_INTERVAL = 10;
    private int auxiliaryPollQueueInterval = DEFAULT_AUXILIARY_POLL_QUEUE_INTERVAL;

    public OracleDataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }

    public void setDataSourceConfig(OracleDataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public String getListenerQuery() {
        return listenerQuery;
    }

    public void setListenerQuery(String listenerQuery) {
        this.listenerQuery = listenerQuery;
    }

    public static OracleDBQueueConfig loadFromProperties(Properties props) {
        OracleDBQueueConfig oracleDBQueueConfig = new OracleDBQueueConfig();
        oracleDBQueueConfig.setListenerQuery(props.getProperty(CONFKEY_LISTENER_QUERY));
        oracleDBQueueConfig.setTableName(props.getProperty(CONFKEY_TABLE_NAME));
        oracleDBQueueConfig.setStatusField(props.getProperty(CONFKEY_STATUS_FIELD));
        if (props.containsKey(CONFKEY_AUXILIARY_POLL_QUEUE_INTERVAL))
            oracleDBQueueConfig.setAuxiliaryPollQueueInterval(
                    PropertiesUtil.getIntegerProperty(props, CONFKEY_AUXILIARY_POLL_QUEUE_INTERVAL, DEFAULT_AUXILIARY_POLL_QUEUE_INTERVAL)
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
                ", listenerQuery='" + listenerQuery + '\'' +
                ", tableName='" + tableName + '\'' +
                ", statusField='" + statusField + '\'' +
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

    public String getStatusValFailed() {
        return statusValFailed;
    }

    public void setStatusValFailed(String statusValFailed) {
        this.statusValFailed = statusValFailed;
    }
}
