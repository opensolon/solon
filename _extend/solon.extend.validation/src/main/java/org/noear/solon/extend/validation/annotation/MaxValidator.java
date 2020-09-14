package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.StringUtils;
import org.noear.solon.extend.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0.28
 * */
public class MaxValidator implements Validator<Max> {
    public static final MaxValidator instance = new MaxValidator();

    @Override
    public String message(Max anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, Max anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        if (StringUtils.isInteger(val) == false || Long.parseLong(val) > anno.value()) {
            tmp.append(',').append(name);
        }

        if (tmp.length() > 1) {
            return XResult.failure(tmp.substring(1));
        } else {
            return XResult.succeed();
        }
    }
}
