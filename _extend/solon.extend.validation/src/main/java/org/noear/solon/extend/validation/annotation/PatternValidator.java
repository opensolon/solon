package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PatternValidator implements Validator<Pattern> {
    public static final PatternValidator instance = new PatternValidator();

    private static final Map<String, java.util.regex.Pattern> cached = new ConcurrentHashMap<>();

    @Override
    public String message(Pattern anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, Pattern anno, String key, StringBuilder tmp) {
        java.util.regex.Pattern pt = cached.get(anno.value());

        if (pt == null) {
            pt = java.util.regex.Pattern.compile(anno.value());
            cached.putIfAbsent(anno.value(), pt);
        }

        String val = ctx.param(key);
        if (val == null || pt.matcher(val).find() == false) {
            tmp.append(',').append(key);
        }

        if (tmp.length() > 1) {
            return XResult.failure(tmp.substring(1));
        } else {
            return XResult.succeed();
        }
    }
}
