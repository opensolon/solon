package org.noear.solon.extend.auth;

/**
 * 认证异常
 *
 * @author noear
 * @since 1.4
 */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
