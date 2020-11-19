package com.roshka.oracledbqueue.task;

import java.time.LocalDateTime;

public class TaskResult {

    public enum Outcome {
        PENDING,
        OK,
        ERROR
    }

    private Outcome outcome;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime t00;
    private LocalDateTime t01;

    public TaskResult(String oldStatus) {
        outcome = Outcome.PENDING;
        t00 = LocalDateTime.now();
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public void setOutcome(Outcome outcome) {
        this.outcome = outcome;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getT00() {
        return t00;
    }

    public void setT00(LocalDateTime t00) {
        this.t00 = t00;
    }

    public LocalDateTime getT01() {
        return t01;
    }

    public void setT01(LocalDateTime t01) {
        this.t01 = t01;
    }

}
