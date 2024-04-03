package org.noear.solon.web.webdav;

import org.noear.solon.exception.SolonException;

public class WebDavActionException extends SolonException {
    private int code;

    public WebDavActionException(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
