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
package org.noear.solon.expression.snel;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 属性持有者
 *
 * @author noear
 * @since 3.1
 */
public class PropertyHolder {
    private MethodHandle methodHandle;
    private Field field;

    public PropertyHolder(MethodHandle methodHandle, Field field) {
        this.methodHandle = methodHandle;
        this.field = field;
    }

    /**
     * 获取属性值
     */
    public Object getValue(Object target) throws Throwable {
        if (methodHandle == null) {
            return field.get(target);
        } else {
            return methodHandle.invoke(target);
        }
    }
}