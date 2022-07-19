package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.util.StringUtils;
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
    public Class<?>[] groups(Min anno) {
        return anno.groups();
    }

    @Override
    public Result validateOfValue(Min anno, Object val0, StringBuilder tmp) {
        if (val0 instanceof Number == false) {
            return Result.failure();
        }

        Number val = (Number) val0;

        if (val == null || val.longValue() < anno.value()) {
            return Result.failure();
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, Min anno, String name, StringBuilder tmp) {
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
