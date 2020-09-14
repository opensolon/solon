package org.noear.solon.extend.validation.annotation;

import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0.28
 * */
public class NotEmptyValidator implements Validator<NotEmpty> {
    public static final NotEmptyValidator instance = new NotEmptyValidator();

    @Override
    public String message(NotEmpty anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, NotEmpty anno, String name, StringBuilder tmp) {
        if (name == null) {
            //来自函数
            for (String key : anno.value()) {
                if (XUtil.isEmpty(ctx.param(key))) {
                    tmp.append(',').append(key);
                }
            }
        } else {
            //来自参数
            if (XUtil.isEmpty(ctx.param(name))) {
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
