package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

import java.util.Collection;

/**
 *
 * @author noear
 * @since 1.5
 * */
public class SizeValidator implements Validator<Size> {
    public static final SizeValidator instance = new SizeValidator();

    @Override
    public String message(Size anno) {
        return anno.message();
    }

    @Override
    public Result validateOfEntity(Size anno, String name, Object val0, StringBuilder tmp) {
        if (val0 instanceof Collection == false) {
            return Result.failure(name);
        }

        Collection val = (Collection) val0;

        if (val == null || (anno.min() > 0 && val.size() < anno.min()) || (anno.max() > 0 && val.size() > anno.max())) {
            return Result.failure(name);
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, Size anno, String name, StringBuilder tmp) {
        return Result.failure();
    }
}
