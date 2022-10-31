package org.noear.solon.auth;

import org.noear.solon.core.handle.Result;

/**
 * 认证状态
 *
 * @author noear
 * @since 1.10
 */
public enum AuthStatus {
    OF_IP(403, " , this ip unauthorized"),
    OF_LOGINED(401, "Unauthorized"),
    OF_PATH(403, "Forbidden"),
    OF_PERMISSIONS(403, "No permission granted"),
    OF_ROLES(403, "No role granted"),
    ;
    public final int code;
    public final String message;

    AuthStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

    /**
     * 转为结果对象
     * */
    public Result toResult() {
        return Result.failure(code, message, this);
    }

    /**
     * 转为结果对象
     *
     * @param note 摘要
     * */
    public Result toResult(String note) {
        return Result.failure(code, note + message, this);
    }
}
