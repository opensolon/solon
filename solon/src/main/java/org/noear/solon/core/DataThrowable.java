package org.noear.solon.core;


import java.io.Serializable;

/**
 * 可抛出的数据
 *
 * */
public class DataThrowable extends RuntimeException implements Serializable {
    public DataThrowable(String message){
        super(message);
    }

    public DataThrowable(String message, Throwable cause){
        super(message, cause);
    }
}
