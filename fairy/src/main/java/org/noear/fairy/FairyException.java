package org.noear.fairy;

public class FairyException extends RuntimeException{
    public FairyException(String message){
        super(message);
    }

    public FairyException(Throwable cause){
        super(cause);
    }
}
