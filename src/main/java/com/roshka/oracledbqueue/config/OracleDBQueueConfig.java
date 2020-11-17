package com.roshka.oracledbqueue.config;

import java.util.Properties;

public class OracleDBQueueConfig {

    public static final String CONFKEY_PREFIX = "odbq";

    private OracleDataSourceConfig dataSourceConfig;

    public static final String CONFKEY_LISTENER_QUERY = String.format("%s.listener_query", CONFKEY_PREFIX);
    private String listenerQuery;

    public static final String CONFKEY_TABLE_NAME = String.format("%s.table_name", CONFKEY_PREFIX);
    private String tableName;

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
}
