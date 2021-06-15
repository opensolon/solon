package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class LengthValidator implements Validator<Length> {
    public static final LengthValidator instance = new LengthValidator();

    @Override
    public String message(Length anno) {
        return anno.message();
    }

    @Override
    public Result validateOfEntity(Length anno, String name, Object val0, StringBuilder tmp) {
        if (val0 instanceof String == false) {
            return Result.failure(name);
        }

        String val = (String) val0;

        if (val == null || val.length() < anno.min() || val.length() > anno.max()) {
            return Result.failure(name);
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, Length anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        if (val == null || val.length() < anno.min() || val.length() > anno.max()) {
            return Result.failure(name);
        } else {
            return Result.succeed();
        }
    }
}
