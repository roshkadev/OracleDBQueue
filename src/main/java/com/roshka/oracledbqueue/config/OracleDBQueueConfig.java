package com.roshka.oracledbqueue.config;

public class OracleDBQueueConfig {

    private OracleDataSourceConfig dataSourceConfig;
    private String listenerQuery;

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
}
