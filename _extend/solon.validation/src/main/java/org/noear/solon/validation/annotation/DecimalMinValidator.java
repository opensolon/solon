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
    public Result validate(Context ctx, DecimalMin anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        if (StringUtils.isNumber(val) == false || Double.parseDouble(val) < anno.value()) {
            tmp.append(',').append(name);
        }

        if (tmp.length() > 1) {
            return Result.failure(tmp.substring(1));
        } else {
            return Result.succeed();
        }
    }
}
