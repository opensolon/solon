package org.noear.solon.ext;


import java.io.Serializable;

/**
 * 可抛出的数据，用于数据传导（以实现两种方案：return data; throw data）
 *
 * @author noear
 * @since 1.0
 * */
public class DataThrowable extends RuntimeException implements Serializable {
    public DataThrowable(String message){
        super(message);
    }

    public DataThrowable(String message, Throwable cause){
        super(message, cause);
    }
}
