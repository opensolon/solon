package org.noear.solon.extend.validation.annotation;

import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

public class NotNullValidator implements Validator<NotNull> {
    public static final NotNullValidator instance = new NotNullValidator();

    @Override
    public XResult validate(XContext ctx, NotNull anno, StringBuilder tmp) {
        for (String key : anno.value()) {
            if (ctx.param(key) == null) {
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
