package org.noear.solon.validation.annotation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

import java.time.format.DateTimeFormatter;

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
    public Result validateOfEntity(Class<?> clz, Length anno, String name, Object val0, StringBuilder tmp) {
        if (val0 != null && val0 instanceof String == false) {
            return Result.failure(clz.getSimpleName() + "." + name);
        }

        String val = (String) val0;

        if (verify(anno, val) == false) {
            return Result.failure(clz.getSimpleName() + "." + name);
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, Length anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        if (verify(anno, val) == false) {
            return Result.failure(name);
        } else {
            return Result.succeed();
        }
    }

    private boolean verify(Length anno, String val) {
        //如果为空，算通过（交由@NotNull之类，进一步控制）
        if (val == null) {
            return true;
        }

        if (anno.min() > 0 && val.length() < anno.min()) {
            return false;
        }

        if (anno.max() > 0 && val.length() > anno.max()) {
            return false;
        }

        return true;
    }
}
