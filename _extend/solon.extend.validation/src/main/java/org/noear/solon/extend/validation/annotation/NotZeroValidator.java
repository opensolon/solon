package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.StringUtils;
import org.noear.solon.extend.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0.24
 * */
public class NotZeroValidator implements Validator<NotZero> {
    public static final NotZeroValidator instance = new NotZeroValidator();

    @Override
    public String message(NotZero anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, NotZero anno, String name, StringBuilder tmp) {
        if (name == null) {
            //来自函数
            for (String key : anno.value()) {
                String val = ctx.param(key);

                if (StringUtils.isInteger(val) == false || Long.parseLong(val) == 0) {
                    tmp.append(',').append(key);
                }
            }
        } else {
            //来自参数
            String val = ctx.param(name);

            if (StringUtils.isInteger(val) == false || Long.parseLong(val) == 0) {
                tmp.append(',').append(name);
            }
        }

        if (tmp.length() > 1) {
            return XResult.failure(tmp.substring(1));
        } else {
            return XResult.succeed();
        }
    }
}
