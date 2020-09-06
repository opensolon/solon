package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

public class SizeValidator implements Validator<Size> {
    public static final SizeValidator instance = new SizeValidator();

    @Override
    public XResult validate(XContext ctx, Size anno, StringBuilder tmp) {
        for (String key : anno.check()) {
            String val = ctx.param(key);

            if (val == null || val.length() < anno.min() || val.length() > anno.max()) {
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
