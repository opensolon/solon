package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class NullValidator implements Validator<Null> {
    public static final NullValidator instance = new NullValidator();

    @Override
    public String message(Null anno) {
        return anno.message();
    }

    @Override
    public Result validateOfValue(String label, Null anno,  Object val, StringBuilder tmp) {
        if (val != null) {
            return Result.failure(label);
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, Null anno, String name, StringBuilder tmp) {
        if (name == null) {
            //来自函数
            for (String key : anno.value()) {
                if (ctx.param(key) != null) {
                    tmp.append(',').append(key);
                }
            }
        } else {
            //来自参数
            if (ctx.param(name) != null) {
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
