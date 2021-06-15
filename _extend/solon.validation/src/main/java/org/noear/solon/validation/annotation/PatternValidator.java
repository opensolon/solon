package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class PatternValidator implements Validator<Pattern> {
    private static final Map<String, java.util.regex.Pattern> cached = new ConcurrentHashMap<>();

    public static final PatternValidator instance = new PatternValidator();

    @Override
    public String message(Pattern anno) {
        return anno.message();
    }

    @Override
    public Result validateOfEntity(Class<?> clz, Pattern anno, String name, Object val0, StringBuilder tmp) {
        if (val0 instanceof String == false) {
            return Result.failure(clz.getSimpleName() + "." + name);
        }

        String val = (String) val0;

        if (val == null || verify(anno, val) == false) {
            return Result.failure(clz.getSimpleName() + "." + name);
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, Pattern anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        if (val == null || verify(anno, val) == false) {
            return Result.failure(name);
        } else {
            return Result.succeed();
        }
    }

    private boolean verify(Pattern anno, String val) {
        java.util.regex.Pattern pt = cached.get(anno.value());

        if (pt == null) {
            pt = java.util.regex.Pattern.compile(anno.value());
            cached.putIfAbsent(anno.value(), pt);
        }

        return pt.matcher(val).find();
    }
}
