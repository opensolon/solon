package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;

/**
 *
 * @author noear
 * @since 1.3
 * */
public interface LoginedChecker {
    boolean check(Logined anno, Context ctx, String userKeyName);
}
