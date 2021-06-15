package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.util.StringUtils;
import org.noear.solon.validation.Validator;
import sun.dc.DuctusRenderingEngine;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class DecimalMaxValidator implements Validator<DecimalMax> {
    public static final DecimalMaxValidator instance = new DecimalMaxValidator();

    @Override
    public String message(DecimalMax anno) {
        return anno.message();
    }

    @Override
    public Result validateOfEntity(Class<?> clz, DecimalMax anno, String name, Object val0, StringBuilder tmp) {
        if (val0 instanceof Double == false) {
            return Result.failure(clz.getSimpleName() + "." + name);
        }

        Double val = (Double) val0;

        if (val == null || val > anno.value()) {
            return Result.failure(clz.getSimpleName() + "." + name);
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, DecimalMax anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        if (StringUtils.isNumber(val) == false || Double.parseDouble(val) > anno.value()) {
            return Result.failure(name);
        } else {
            return Result.succeed();
        }
    }
}
