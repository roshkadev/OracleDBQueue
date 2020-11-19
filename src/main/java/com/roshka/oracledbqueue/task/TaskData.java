package com.roshka.oracledbqueue.task;

import oracle.sql.ROWID;

import java.util.Map;

public class TaskData {

    private ROWID rowid;
    private String currentStatus;
    private Map<String, Object> data;

    public ROWID getRowid() {
        return rowid;
    }

    public void setRowid(ROWID rowid) {
        this.rowid = rowid;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    @Override
    public String toString() {
        return "TaskData{" +
                "rowid=" + rowid +
                ", currentStatus=" + currentStatus +
                ", data=" + toStringMap(data) +
                '}';
    }

    public String toStringMap(Map<String, Object> data) {
        if (data != null) {
            StringBuilder sb = new StringBuilder("{");
            for (String key : data.keySet()) {
                Object val = data.get(key);
                sb.append(key).append("=").append(val).append(",");
            }
            if (sb.length()>1) {
                sb.replace(sb.length()-1, sb.length()-1, "}");
            } else {
                sb.append('}');
            }
            return sb.toString();
        } else {
            return null;
        }
    }

}
