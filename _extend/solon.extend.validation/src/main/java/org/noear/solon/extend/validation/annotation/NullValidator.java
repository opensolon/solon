package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0.24
 * */
public class NullValidator implements Validator<Null> {
    public static final NullValidator instance = new NullValidator();

    @Override
    public String message(Null anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, Null anno, String name, StringBuilder tmp) {
        if (name == null) {
            //来自函数
            for (String key : anno.value()) {
                if (ctx.param(key) != null) {
                    tmp.append(',').append(key);
                }
            }
        } else {
            //来自参数
            if (ctx.param(name) != null) {
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
