package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0.28
 * */
public class LengthValidator implements Validator<Length> {
    public static final LengthValidator instance = new LengthValidator();

    @Override
    public String message(Length anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, Length anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        if (val == null || val.length() < anno.min() || val.length() > anno.max()) {
            tmp.append(',').append(name);
        }

        if (tmp.length() > 1) {
            return XResult.failure(tmp.substring(1));
        } else {
            return XResult.succeed();
        }
    }
}
