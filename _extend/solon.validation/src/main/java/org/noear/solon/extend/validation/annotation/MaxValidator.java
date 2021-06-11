package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.validation.StringUtils;
import org.noear.solon.extend.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class MaxValidator implements Validator<Max> {
    public static final MaxValidator instance = new MaxValidator();

    @Override
    public String message(Max anno) {
        return anno.message();
    }

    @Override
    public Result validate(Context ctx, Max anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        if (StringUtils.isInteger(val) == false || Long.parseLong(val) > anno.value()) {
            tmp.append(',').append(name);
        }

        if (tmp.length() > 1) {
            return Result.failure(tmp.substring(1));
        } else {
            return Result.succeed();
        }
    }
}
