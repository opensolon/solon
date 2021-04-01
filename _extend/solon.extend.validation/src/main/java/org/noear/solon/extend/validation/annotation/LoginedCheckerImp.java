package org.noear.solon.extend.validation.annotation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2021/4/1 created
 */
public class LoginedCheckerImp implements LoginedChecker{
    @Override
    public boolean check(Logined anno, Context ctx, String userKeyName) {
        Object userKey = ctx.session(userKeyName);

        if (userKey == null) {
            return false;
        }

        if (userKey instanceof Number) {
            if (((Number) userKey).longValue() < 1) {
                return false;
            }
        }

        if (userKey instanceof String) {
            if (((String) userKey).length() < 1) {
                return false;
            }
        }

        return true;
    }
}
