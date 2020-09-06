package org.noear.solon.extend.validation.annotation;

import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PatternValidator implements Validator<Pattern> {
    public static final PatternValidator instance = new PatternValidator();

    private static final Map<String, java.util.regex.Pattern> cached = new ConcurrentHashMap<>();

    @Override
    public XResult validate(XContext ctx, Pattern anno, StringBuilder tmp) {
        java.util.regex.Pattern pt = cached.get(anno.expr());

        if (pt == null) {
            pt = java.util.regex.Pattern.compile(anno.expr());
            cached.putIfAbsent(anno.expr(), pt);
        }

        for (String key : anno.value()) {
            String val = ctx.param(key);
            if (val == null || pt.matcher(val).find() == false) {
                tmp.append(',').append(key);
            }
        }

        if (tmp.length() > 1) {
            if (XUtil.isNotEmpty(anno.message())) {
                return XResult.failure(anno.message());
            } else {
                return XResult.failure(tmp.substring(1));
            }
        } else {
            return XResult.succeed();
        }
    }
}
