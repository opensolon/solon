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
package org.noear.solon.core.wrap;

import org.noear.solon.lang.Nullable;

import java.lang.reflect.ParameterizedType;

/**
 * 字段 变量描述符
 *
 * @author noear
 * @since 2.4
 */
public class FieldWrapSpec extends VarSpecBase {
    private final FieldWrap fieldWrap;

    public FieldWrapSpec(FieldWrap fieldWrap) {
        super(fieldWrap.getField(), fieldWrap.getField().getName());
        this.fieldWrap = fieldWrap;
    }


    @Override
    public @Nullable ParameterizedType getGenericType() {
        return fieldWrap.getGenericType();
    }

    @Override
    public Class<?> getType() {
        return fieldWrap.getType();
    }
}
