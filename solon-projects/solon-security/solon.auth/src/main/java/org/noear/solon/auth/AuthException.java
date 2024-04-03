package org.noear.solon.auth;

import org.noear.solon.exception.SolonException;

/**
 * 认证异常
 *
 * @author noear
 * @since 1.4
 */
public class AuthException extends SolonException {
    private final AuthStatus status;

    public AuthStatus getStatus() {
        return status;
    }

    public AuthException(AuthStatus status, String message) {
        super(message);
        this.status = status;
    }
}
