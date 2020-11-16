package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class NotNullValidator implements Validator<NotNull> {
    public static final NotNullValidator instance = new NotNullValidator();

    @Override
    public String message(NotNull anno) {
        return anno.message();
    }

    @Override
    public Result validate(Context ctx, NotNull anno, String name, StringBuilder tmp) {
        if(name == null) {
            //来自函数
            for (String key : anno.value()) {
                if (ctx.param(key) == null) {
                    tmp.append(',').append(key);
                }
            }
        }else{
            //来自参数
            if (ctx.param(name) == null) {
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
