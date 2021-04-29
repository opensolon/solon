package org.noear.solon.ext;

import java.io.Serializable;

/**
 * @author noear
 * @since 1.3
 */
public class WarnThrowable extends RuntimeException implements Serializable {
    private Object attachment;

    /**
     * 附件
     * */
    public Object attachment(){
        return attachment;
    }

    public WarnThrowable() {
        super();
    }

    public WarnThrowable(Throwable cause) {
        super(cause);
    }

    public WarnThrowable(String message) {
        super(message);
    }

    public WarnThrowable(String message, Throwable cause) {
        super(message, cause);
    }

    public WarnThrowable(Object attachment, String message) {
        super(message);
        this.attachment = attachment;
    }
}
