package org.noear.solon.extend.validation.annotation;

import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

public class NotEmptyValidator implements Validator<NotEmpty> {
    public static final NotEmptyValidator instance = new NotEmptyValidator();

    @Override
    public String message(NotEmpty anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, NotEmpty anno, String name, StringBuilder tmp) {
        for (String key : anno.value()) {
            if (XUtil.isEmpty(ctx.param(key))) {
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
