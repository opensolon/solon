package org.noear.solon.validation.annotation;

import org.noear.solon.Utils;
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
    public Result validateOfValue(String label, Pattern anno, Object val0, StringBuilder tmp) {
        if (val0 != null && val0 instanceof String == false) {
            return Result.failure(label);
        }

        String val = (String) val0;

        if (verify(anno, val) == false) {
            return Result.failure(label);
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, Pattern anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        if (verify(anno, val) == false) {
            return Result.failure(name);
        } else {
            return Result.succeed();
        }
    }

    private boolean verify(Pattern anno, String val) {
        //如果为空，算通过（交由@NotEmpty之类，进一步控制）
        if (Utils.isEmpty(val)) {
            return true;
        }

        java.util.regex.Pattern pt = cached.get(anno.value());

        if (pt == null) {
            pt = java.util.regex.Pattern.compile(anno.value());
            cached.putIfAbsent(anno.value(), pt);
        }

        return pt.matcher(val).find();
    }
}
