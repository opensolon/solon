package org.noear.solon.validation.annotation;

import org.noear.solon.Utils;
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
    public Class<?>[] groups(DecimalMin anno) {
        return anno.groups();
    }

    @Override
    public Result validateOfValue(DecimalMin anno, Object val0, StringBuilder tmp) {
        if (val0 != null && val0 instanceof Number == false) {
            return Result.failure();
        }

        Number val = (Number) val0;

        if (verify(anno, val) == false) {
            return Result.failure();
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, DecimalMin anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        //如果为空，算通过（交由 @NotNull 或 @NotEmpty 或 @NotBlank 进一步控制）
        if (Utils.isEmpty(val)) {
            return Result.succeed();
        }

        if (StringUtils.isNumber(val) == false) {
            return Result.failure(name);
        }

        if (verify(anno, Double.parseDouble(val)) == false) {
            return Result.failure(name);
        } else {
            return Result.succeed();
        }
    }

    private boolean verify(DecimalMin anno, Number val) {
        //如果为空，算通过（交由 @NotNull 进一步控制）
        if (val == null) {
            return true;
        }

        return val.doubleValue() >= anno.value();
    }
}
