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
                '}';
    }

    public int getAuxiliaryPollQueueInterval() {
        return auxiliaryPollQueueInterval;
    }

    public void setAuxiliaryPollQueueInterval(int auxiliaryPollQueueInterval) {
        this.auxiliaryPollQueueInterval = auxiliaryPollQueueInterval;
    }
}
