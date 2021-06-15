package org.noear.solon.validation.annotation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class NotEmptyValidator implements Validator<NotEmpty> {
    public static final NotEmptyValidator instance = new NotEmptyValidator();

    @Override
    public String message(NotEmpty anno) {
        return anno.message();
    }

    @Override
    public Result validateOfEntity(Class<?> clz, NotEmpty anno, String name, Object val0, StringBuilder tmp) {
        if (val0 instanceof String == false) {
            return Result.failure(clz.getSimpleName() + "." + name);
        }

        String val = (String) val0;

        if (Utils.isEmpty(val)) {
            return Result.failure(clz.getSimpleName() + "." + name);
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, NotEmpty anno, String name, StringBuilder tmp) {
        if (name == null) {
            //来自函数
            for (String key : anno.value()) {
                if (Utils.isEmpty(ctx.param(key))) {
                    tmp.append(',').append(key);
                }
            }
        } else {
            //来自参数
            if (Utils.isEmpty(ctx.param(name))) {
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
