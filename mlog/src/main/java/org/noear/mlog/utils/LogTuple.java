package org.noear.mlog.utils;

/**
 * @author org.slf4j.helpers
 */
public class LogTuple {
    public static LogTuple NULL = new LogTuple((String)null);
    private String message;
    private Throwable throwable;
    private Object[] argArray;

    public LogTuple(String message) {
        this(message, (Object[])null, (Throwable)null);
    }

    public LogTuple(String message, Object[] argArray, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
        this.argArray = argArray;
    }

    public String getMessage() {
        return this.message;
    }

    public Object[] getArgArray() {
        return this.argArray;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }
}
