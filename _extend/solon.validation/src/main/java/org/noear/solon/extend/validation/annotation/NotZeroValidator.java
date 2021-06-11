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
public class NotZeroValidator implements Validator<NotZero> {
    public static final NotZeroValidator instance = new NotZeroValidator();

    @Override
    public String message(NotZero anno) {
        return anno.message();
    }

    @Override
    public Result validate(Context ctx, NotZero anno, String name, StringBuilder tmp) {
        if (name == null) {
            //来自函数
            for (String key : anno.value()) {
                String val = ctx.param(key);

                if (StringUtils.isInteger(val) == false || Long.parseLong(val) == 0) {
                    tmp.append(',').append(key);
                }
            }
        } else {
            //来自参数
            String val = ctx.param(name);

            if (StringUtils.isInteger(val) == false || Long.parseLong(val) == 0) {
                tmp.append(',').append(name);
            }
        }

        if (tmp.length() > 1) {
            return Result.failure(tmp.substring(1));
        } else {
            return Result.succeed();
        }
    }
}
