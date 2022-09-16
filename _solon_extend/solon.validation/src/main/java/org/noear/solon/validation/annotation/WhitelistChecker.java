package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;

/**
 *
 * @author noear
 * @since 1.0
 * */
public interface WhitelistChecker {
    /**
     * @param anno 注解
     * @param ctx 上下文
     *
     * @return 是白名单
     * */
    boolean check(Whitelist anno, Context ctx);
}
