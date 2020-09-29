package org.noear.fairy;

/**
 * Fairy - 异常
 *
 * @author noear
 * @since 1.0
 * */
public class FairyException extends RuntimeException{
    public FairyException(String message){
        super(message);
    }

    public FairyException(Throwable cause){
        super(cause);
    }
}
