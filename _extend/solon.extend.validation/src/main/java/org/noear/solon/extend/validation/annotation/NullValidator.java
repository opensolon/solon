package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

public class NullValidator implements Validator<Null> {
    public static final NullValidator instance = new NullValidator();

    @Override
    public XResult validate(XContext ctx, Null anno, StringBuilder tmp) {
        for (String key : anno.value()) {
            if (ctx.param(key) != null) {
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
