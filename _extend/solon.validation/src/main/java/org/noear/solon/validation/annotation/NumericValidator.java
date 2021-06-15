package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.util.StringUtils;
import org.noear.solon.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class NumericValidator implements Validator<Numeric> {
    public static final NumericValidator instance = new NumericValidator();

    @Override
    public String message(Numeric anno) {
        return anno.message();
    }

    @Override
    public Result validateOfContext(Context ctx, Numeric anno, String name, StringBuilder tmp) {
        if (name == null) {
            //来自函数
            for (String key : anno.value()) {
                String val = ctx.param(key);

                if (StringUtils.isNumber(val) == false) {
                    tmp.append(',').append(key);
                }
            }
        } else {
            //来自参数
            String val = ctx.param(name);

            if (StringUtils.isNumber(val) == false) {
                tmp.append(',').append(name);
            }
        }

        if (tmp.length() > 1) {
            return Result.failure(tmp.substring(1));
        } else {
            return Result.succeed();
        }
    }
}
