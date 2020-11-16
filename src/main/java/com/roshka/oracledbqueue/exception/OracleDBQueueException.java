package com.roshka.oracledbqueue.exception;

public class OracleDBQueueException extends Exception {

    private String code;

    public OracleDBQueueException(String code) {
        this(code, null);
    }

    public OracleDBQueueException(String code, String message) {
        this(code, message, null);
    }

    public OracleDBQueueException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
