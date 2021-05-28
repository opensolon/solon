package org.noear.solon.extend.auth;

/**
 * @author noear
 * @since 1.4
 */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }
}
