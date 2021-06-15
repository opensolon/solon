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
public class DecimalMinValidator implements Validator<DecimalMin> {
    public static final DecimalMinValidator instance = new DecimalMinValidator();

    @Override
    public String message(DecimalMin anno) {
        return anno.message();
    }

    @Override
    public Result validateOfEntity(Class<?> clz, DecimalMin anno, String name, Object val0, StringBuilder tmp) {
        if (val0 instanceof Double == false) {
            return Result.failure(clz.getSimpleName() + "." + name);
        }

        Double val = (Double) val0;

        if (val == null || val < anno.value()) {
            return Result.failure(clz.getSimpleName() + "." + name);
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, DecimalMin anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        if (StringUtils.isNumber(val) == false || Double.parseDouble(val) < anno.value()) {
            return Result.failure(name);
        } else {
            return Result.succeed();
        }
    }
}
