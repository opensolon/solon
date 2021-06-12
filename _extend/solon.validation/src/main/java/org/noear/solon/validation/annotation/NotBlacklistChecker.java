package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;

/**
 *
 * @author noear
 * @since 1.3
 * */
@FunctionalInterface
public interface NotBlacklistChecker {
    boolean check(NotBlacklist anno, Context ctx);
}
