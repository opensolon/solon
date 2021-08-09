package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;

/**
 *
 * @author noear
 * @since 1.3
 * */
@FunctionalInterface
public interface NotBlacklistChecker {
    /**
     * @param anno 注解
     * @param ctx 上下文
     * */
    boolean check(NotBlacklist anno, Context ctx);
}
