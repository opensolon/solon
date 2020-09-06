package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

public class MinValidator implements Validator<Min> {
    public static final MinValidator instance = new MinValidator();

    @Override
    public XResult validate(XContext ctx, Min anno, StringBuilder tmp) {
        for (String key : anno.value()) {
            if (ctx.paramAsLong(key) < anno.min()) {
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
