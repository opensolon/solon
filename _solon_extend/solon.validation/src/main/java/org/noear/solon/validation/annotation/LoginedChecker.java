package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;

/**
 *
 * @author noear
 * @since 1.3
 * */
public interface LoginedChecker {
    /**
     * @param anno 注解
     * @param ctx 上下文
     * @param userKeyName 用户标识名
     *
     * @return 已登录
     * */
    boolean check(Logined anno, Context ctx, String userKeyName);
}
