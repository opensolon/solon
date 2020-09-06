package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

public class NotNullValidator implements Validator<NotNull> {
    public static final NotNullValidator instance = new NotNullValidator();

    @Override
    public XResult validate(XContext ctx, NotNull anno, StringBuilder tmp) {
        for (String key : anno.names()) {
            if (ctx.param(key) == null) {
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
