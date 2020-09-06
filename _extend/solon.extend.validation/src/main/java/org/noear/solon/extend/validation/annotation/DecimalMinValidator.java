package org.noear.solon.extend.validation.annotation;

import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

public class DecimalMinValidator implements Validator<DecimalMin> {
    public static final DecimalMinValidator instance = new DecimalMinValidator();

    @Override
    public XResult validate(XContext ctx, DecimalMin anno, StringBuilder tmp) {
        for (String key : anno.value()) {
            if (ctx.paramAsDouble(key) < anno.min()) {
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
