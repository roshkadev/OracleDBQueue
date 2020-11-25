package com.roshka.oracledbqueue.task;

import java.sql.RowId;
import java.time.LocalDateTime;
import java.util.Map;

public class TaskData {

    private LocalDateTime tq;
    private LocalDateTime t0;
    private LocalDateTime t1;

    private String threadName;
    private TaskManager.TaskQueueType taskQueueType;

    public TaskData(TaskManager.TaskQueueType taskQueueType) {
        this.taskQueueType = taskQueueType;
    }

    private RowId rowid;
    private String currentStatus;
    private Map<String, Object> data;

    public RowId getRowid() {
        return rowid;
    }

    public void setRowid(RowId rowid) {
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

    public LocalDateTime getTq() {
        return tq;
    }

    public void setTq(LocalDateTime tq) {
        this.tq = tq;
    }

    public LocalDateTime getT0() {
        return t0;
    }

    public void setT0(LocalDateTime t0) {
        this.t0 = t0;
    }

    public LocalDateTime getT1() {
        return t1;
    }

    public void setT1(LocalDateTime t1) {
        this.t1 = t1;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public TaskManager.TaskQueueType getTaskQueueType() {
        return taskQueueType;
    }

    public void setTaskQueueType(TaskManager.TaskQueueType taskQueueType) {
        this.taskQueueType = taskQueueType;
    }
}
