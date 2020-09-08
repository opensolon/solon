package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.StringUtils;
import org.noear.solon.extend.validation.Validator;

public class MinValidator implements Validator<Min> {
    public static final MinValidator instance = new MinValidator();

    @Override
    public String message(Min anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, Min anno, String key, StringBuilder tmp) {
        String val = ctx.param(key);

        if (StringUtils.isInteger(val) == false || Long.parseLong(key) < anno.value()) {
            tmp.append(',').append(key);
        }

        if (tmp.length() > 1) {
            return XResult.failure(tmp.substring(1));
        } else {
            return XResult.succeed();
        }
    }
}
