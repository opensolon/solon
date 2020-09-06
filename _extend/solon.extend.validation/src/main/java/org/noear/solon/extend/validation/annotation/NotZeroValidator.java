package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

public class NotZeroValidator implements Validator<NotZero> {
    public static final NotZeroValidator instance = new NotZeroValidator();

    @Override
    public String message(NotZero anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, NotZero anno, StringBuilder tmp) {
        for (String key : anno.value()) {
            if (ctx.paramAsLong(key) == 0) {
                tmp.append(',').append(key);
            }
        }

        if (tmp.length() > 1) {
            return XResult.failure(tmp.substring(1));
        } else {
            return XResult.succeed();
        }
    }
}
