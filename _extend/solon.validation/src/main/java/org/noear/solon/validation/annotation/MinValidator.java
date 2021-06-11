package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.StringUtils;
import org.noear.solon.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class MinValidator implements Validator<Min> {
    public static final MinValidator instance = new MinValidator();

    @Override
    public String message(Min anno) {
        return anno.message();
    }

    @Override
    public Result validate(Context ctx, Min anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        if (StringUtils.isInteger(val) == false || Long.parseLong(val) < anno.value()) {
            tmp.append(',').append(name);
        }

        if (tmp.length() > 1) {
            return Result.failure(tmp.substring(1));
        } else {
            return Result.succeed();
        }
    }
}
