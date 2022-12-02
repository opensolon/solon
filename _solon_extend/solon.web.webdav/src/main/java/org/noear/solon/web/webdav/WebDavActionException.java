package org.noear.solon.web.webdav;

public class WebDavActionException extends RuntimeException {
    private int code;
    public WebDavActionException(int code){
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
}
