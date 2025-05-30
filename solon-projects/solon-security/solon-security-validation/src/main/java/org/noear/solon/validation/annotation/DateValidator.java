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

import java.time.format.DateTimeFormatter;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class DateValidator implements Validator<Date> {
    public static final DateValidator instance = new DateValidator();


    @Override
    public String message(Date anno) {
        return anno.message();
    }

    @Override
    public Class<?>[] groups(Date anno) {
        return anno.groups();
    }

    @Override
    public boolean isSupportValueType(Class<?> type) {
        return String.class.isAssignableFrom(type);
    }

    /**
     * 校验实体的字段
     * */
    @Override
    public Result validateOfValue(Date anno, Object val0, StringBuilder tmp) {
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

    /**
     * 校验上下文的参数
     * */
    @Override
    public Result validateOfContext(Context ctx, Date anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        if (verify(anno, val) == false) {
            return Result.failure(name);
        } else {
            return Result.succeed();
        }
    }

    private boolean verify(Date anno, String val) {
        //如果为空，算通过（交由 @NotNull 或 @NotEmpty 或 @NotBlank 进一步控制）
        if (Utils.isEmpty(val)) {
            return true;
        }

        try {
            if (Utils.isEmpty(anno.value())) {
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(val);
            } else {
                DateTimeFormatter.ofPattern(anno.value()).parse(val);
            }

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
