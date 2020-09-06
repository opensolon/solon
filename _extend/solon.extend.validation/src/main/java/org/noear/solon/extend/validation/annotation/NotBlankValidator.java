package org.noear.solon.extend.validation.annotation;

import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

public class NotBlankValidator implements Validator<NotBlank> {
    public static final NotBlankValidator instance = new NotBlankValidator();

    @Override
    public String message(NotBlank anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, NotBlank anno, StringBuilder tmp) {
        for (String key : anno.value()) {
            if (XUtil.isBlank(ctx.param(key))) {
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
