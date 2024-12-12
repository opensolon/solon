/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.validation.util.StringUtils;
import org.noear.solon.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class NotZeroValidator implements Validator<NotZero> {
    public static final NotZeroValidator instance = new NotZeroValidator();

    @Override
    public String message(NotZero anno) {
        return anno.message();
    }

    @Override
    public Class<?>[] groups(NotZero anno) {
        return anno.groups();
    }

    @Override
    public boolean isSupportValueType(Class<?> type) {
        return ClassUtil.isNumberType(type);
    }

    @Override
    public Result validateOfValue(NotZero anno, Object val0, StringBuilder tmp) {
        if (val0 instanceof Number == false) {
            return Result.failure();
        }

        Number val = (Number) val0;

        if (val == null || val.longValue() == 0) {
            return Result.failure();
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, NotZero anno, String name, StringBuilder tmp) {
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
            return Result.failure(tmp.substring(1));
        } else {
            return Result.succeed();
        }
    }
}
