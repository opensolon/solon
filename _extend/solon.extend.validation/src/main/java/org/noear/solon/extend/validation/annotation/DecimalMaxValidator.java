package org.noear.solon.extend.validation.annotation;

import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

public class DecimalMaxValidator implements Validator<DecimalMax> {
    public static final DecimalMaxValidator instance = new DecimalMaxValidator();

    @Override
    public String message(DecimalMax anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, DecimalMax anno, StringBuilder tmp) {
        for (String key : anno.value()) {
            if (ctx.paramAsDouble(key) > anno.max()) {
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
