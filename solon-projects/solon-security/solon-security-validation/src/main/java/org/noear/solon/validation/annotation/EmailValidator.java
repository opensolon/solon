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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class EmailValidator implements Validator<Email> {
    private static final Map<String, java.util.regex.Pattern> cached = new ConcurrentHashMap<>();

    public static final EmailValidator instance = new EmailValidator();

    public EmailValidator() {
        cached.putIfAbsent("", java.util.regex.Pattern.compile("^[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)*@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"));
    }

    @Override
    public String message(Email anno) {
        return anno.message();
    }

    @Override
    public Class<?>[] groups(Email anno) {
        return anno.groups();
    }

    @Override
    public boolean isSupportValueType(Class<?> type) {
        return String.class.isAssignableFrom(type);
    }

    @Override
    public Result validateOfValue(Email anno, Object val0, StringBuilder tmp) {
        if (val0 != null && val0 instanceof String == false) {
            return Result.failure();
        }

        String val = (String) val0;

        if (verify(anno, val) == false) {
            return Result.failure();
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, Email anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        if (verify(anno, val) == false) {
            return Result.failure(name);
        } else {
            return Result.succeed();
        }
    }

    private boolean verify(Email anno, String val) {
        //如果为空，算通过（交由 @NotNull 或 @NotEmpty 或 @NotBlank 进一步控制）
        if (Utils.isEmpty(val)) {
            return true;
        }

        java.util.regex.Pattern pt = cached.get(anno.value());

        if (pt == null) {
            if (anno.value().contains("@") == false) {
                throw new IllegalArgumentException("@Email value must have an @ sign");
            }

            pt = java.util.regex.Pattern.compile(anno.value());
            cached.putIfAbsent(anno.value(), pt);
        }

        return pt.matcher(val).find();
    }
}
