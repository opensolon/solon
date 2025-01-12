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
package org.noear.solon.validation;

import org.noear.solon.core.handle.Result;

/**
 * Bean 验证默认实现
 *
 * @author noear
 * @since 1.5
 */
public class BeanValidatorDefault implements BeanValidator {
    /**
     * 验证
     *
     * @param obj    实体对象
     * @param groups 分组（有些实现，可能不支持）
     * @return 验证结果
     */
    @Override
    public Result validate(Object obj, Class<?>... groups) {
        return ValidatorManager.validateOfEntity(obj, groups);
    }
}
