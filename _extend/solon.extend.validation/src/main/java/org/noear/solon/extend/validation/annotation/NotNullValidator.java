package org.noear.solon.extend.validation.annotation;

import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

public class NotNullValidator implements Validator<NotNull> {
    public static final NotNullValidator instance = new NotNullValidator();

    @Override
    public String message(NotNull anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, NotNull anno, String name, StringBuilder tmp) {
        if(name == null) {
            //来自函数
            for (String key : anno.value()) {
                if (ctx.param(key) == null) {
                    tmp.append(',').append(key);
                }
            }
        }else{
            //来自参数
            if (ctx.param(name) == null) {
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
