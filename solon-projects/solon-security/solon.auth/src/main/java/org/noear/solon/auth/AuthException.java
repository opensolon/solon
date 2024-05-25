package org.noear.solon.auth;

import org.noear.solon.core.exception.StatusException;

/**
 * 认证异常
 *
 * @author noear
 * @since 1.4
 */
public class AuthException extends StatusException {
    private final AuthStatus status;

    public AuthStatus getStatus() {
        return status;
    }

    public AuthException(AuthStatus status, String message) {
        super(message, status.code);
        this.status = status;
    }
}
