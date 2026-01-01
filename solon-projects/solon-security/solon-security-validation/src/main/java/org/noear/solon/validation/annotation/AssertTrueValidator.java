/*
 * Copyright 2017-2025 noear.org and authors
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

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

/**
 *
 * @author noear
 * @since 3.0
 * */
public class AssertTrueValidator implements Validator<AssertTrue> {
    public static final AssertTrueValidator instance = new AssertTrueValidator();

    @Override
    public String message(AssertTrue anno) {
        return anno.message();
    }

    @Override
    public Class<?>[] groups(AssertTrue anno) {
        return anno.groups();
    }

    @Override
    public boolean isSupportValueType(Class<?> type) {
        return Boolean.class.isAssignableFrom(type) || type == boolean.class;
    }

    @Override
    public Result validateOfValue(AssertTrue anno, Object val, StringBuilder tmp) {
        if (val == null) {
            return Result.failure();
        }

        if (val instanceof Boolean) {
            if (Boolean.TRUE.equals(val)) {
                return Result.succeed();
            } else {
                return Result.failure();
            }
        }

        return Result.failure();
    }

    @Override
    public Result validateOfContext(Context ctx, AssertTrue anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        //如果为空，算通过（交由 @NotNull 或 @NotEmpty 或 @NotBlank 进一步控制）
        if (Utils.isEmpty(val)) {
            return Result.succeed();
        }

        if (verify(val) == false) {
            return Result.failure(name);
        } else {
            return Result.succeed();
        }
    }

    private boolean verify(String val) {
        //支持 "true", "1", "yes", "on" 等常见的 true 值表示
        if ("true".equalsIgnoreCase(val) || "1".equals(val) || "yes".equalsIgnoreCase(val) || "on".equalsIgnoreCase(val)) {
            return true;
        }

        return false;
    }
}
