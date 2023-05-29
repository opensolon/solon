package org.noear.nami;

/**
 * Nami - 异常
 *
 * @author noear
 * @since 1.0
 * */
public class NamiException extends RuntimeException{
    public NamiException(String message){
        super(message);
    }

    public NamiException(Throwable cause){
        super(cause);
    }
}
