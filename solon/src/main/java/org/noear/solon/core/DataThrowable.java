package org.noear.solon.core;


/**
 * 数据抛出
 *
 * 利于抛出传达数据（将会是很有意思的特性）
 * */
public class DataThrowable extends RuntimeException{
    public DataThrowable(){
        super();
    }

    public DataThrowable(String message) {
        super(message);
    }

    public DataThrowable(Throwable cause) {
        super(cause);
    }
}
