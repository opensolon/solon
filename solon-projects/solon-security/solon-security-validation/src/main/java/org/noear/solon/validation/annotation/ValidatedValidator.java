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

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.BeanValidator;
import org.noear.solon.validation.BeanValidatorDefault;
import org.noear.solon.validation.Validator;

/**
 * @author noear
 * @since 1.5
 */
public class ValidatedValidator implements Validator<Validated> {
    public static final ValidatedValidator instance = new ValidatedValidator();

    private BeanValidator validator = new BeanValidatorDefault();

    public void setValidator(BeanValidator validator) {
        if (validator != null) {
            this.validator = validator;
        }
    }

    @Override
    public Class<?>[] groups(Validated anno) {
        return anno.value();
    }

    @Override
    public Result validateOfValue(Validated anno, Object val, StringBuilder tmp) {
        return validator.validate(val, anno.value());
    }

    @Override
    public Result validateOfContext(Context ctx, Validated anno, String name, StringBuilder tmp) {
        return null;
    }
}
