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
public class EmailValidator implements Validator<Email> {
    private static final Map<String, java.util.regex.Pattern> cached = new ConcurrentHashMap<>();

    public static final EmailValidator instance = new EmailValidator();

    public EmailValidator() {
        cached.putIfAbsent("", java.util.regex.Pattern.compile("^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\\.][a-z]{2,3}([\\.][a-z]{2})?$"));
    }

    @Override
    public String message(Email anno) {
        return anno.message();
    }

    @Override
    public Result validateOfEntity(Class<?> clz, Email anno, String name, Object val0, StringBuilder tmp) {
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
    public Result validateOfContext(Context ctx, Email anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        if (verify(anno, val) == false) {
            return Result.failure(name);
        } else {
            return Result.succeed();
        }
    }

    private boolean verify(Email anno, String val) {
        //如果为空，算通过（交由@NotEmpty之类，进一步控制）
        if (Utils.isEmpty(val)) {
            return true;
        }

        java.util.regex.Pattern pt = cached.get(anno.value());

        if (pt == null) {
            if (anno.value().contains("@") == false) {
                throw new IllegalArgumentException("@Email value must have an @ sign");
            }

            pt = java.util.regex.Pattern.compile(anno.value());
            cached.putIfAbsent(anno.value(), pt);
        }

        return pt.matcher(val).find();
    }
}
