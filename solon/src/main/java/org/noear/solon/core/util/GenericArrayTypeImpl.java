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
package org.noear.solon.core.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * GenericArrayType 实现
 *
 * @author noear
 * @since 3.6
 */
public class GenericArrayTypeImpl implements GenericArrayType {
    private final Type genericComponentType;

    public GenericArrayTypeImpl(Type componentType) {
        this.genericComponentType = componentType;
    }

    public Type getGenericComponentType() {
        return this.genericComponentType;
    }

    public String toString() {
        Type typeTmp = this.getGenericComponentType();
        StringBuilder buf = new StringBuilder();
        if (typeTmp instanceof Class) {
            buf.append(((Class)typeTmp).getName());
        } else {
            buf.append(typeTmp);
        }

        buf.append("[]");
        return buf.toString();
    }

    public boolean equals(Object var1) {
        if (var1 instanceof GenericArrayType) {
            GenericArrayType var2 = (GenericArrayType)var1;
            return Objects.equals(this.genericComponentType, var2.getGenericComponentType());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.genericComponentType);
    }
}