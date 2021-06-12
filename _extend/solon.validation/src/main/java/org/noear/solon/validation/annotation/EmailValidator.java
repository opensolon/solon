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
public class EmailValidator implements Validator<Email> {
    private static final Map<String, java.util.regex.Pattern> cached = new ConcurrentHashMap<>();

    public static final EmailValidator instance = new EmailValidator();

    public EmailValidator(){
        cached.putIfAbsent("",java.util.regex.Pattern.compile("^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\\.][a-z]{2,3}([\\.][a-z]{2})?$"));
    }

    @Override
    public String message(Email anno) {
        return anno.message();
    }

    @Override
    public Result validate(Context ctx, Email anno, String name, StringBuilder tmp) {
        java.util.regex.Pattern pt = cached.get(anno.value());

        if (pt == null) {
            if(anno.value().contains("@") == false){
                throw new RuntimeException("@Email value must have an @ sign");
            }

            pt = java.util.regex.Pattern.compile(anno.value());
            cached.putIfAbsent(anno.value(), pt);
        }

        String val = ctx.param(name);
        if (val == null || pt.matcher(val).find() == false) {
            tmp.append(',').append(name);
        }

        if (tmp.length() > 1) {
            return Result.failure(tmp.substring(1));
        } else {
            return Result.succeed();
        }
    }
}
