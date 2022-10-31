package org.noear.solon.auth;

/**
 * 认证异常
 *
 * @author noear
 * @since 1.4
 */
public class AuthException extends RuntimeException {
    private final AuthStatus status;

    public AuthStatus getStatus() {
        return status;
    }

    public AuthException(AuthStatus status, String message) {
        super(message);
        this.status = status;
    }
}
