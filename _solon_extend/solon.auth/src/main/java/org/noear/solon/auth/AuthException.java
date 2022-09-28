package org.noear.solon.auth;

/**
 * 认证异常
 *
 * @author noear
 * @since 1.4
 */
public class AuthException extends RuntimeException {
    private final int code;

    /**
     * 状态码
     * */
    public int getCode() {
        return code;
    }

    public AuthException(int code, String message) {
        super(message);
        this.code = code;
    }
}
