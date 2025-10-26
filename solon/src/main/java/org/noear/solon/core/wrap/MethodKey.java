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

import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.lang.Nullable;

import java.lang.reflect.Method;

/**
 * 方法键（一般用于 map、cache key）
 *
 * @author noear
 * @since 2.8
 */
public class MethodKey implements Comparable<MethodKey> {
    private final Method method;
    @Nullable
    private final Class<?> targetClass;

    public MethodKey(Method method, @Nullable Class<?> targetClass) {
        this.method = method;
        this.targetClass = targetClass;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof MethodKey)) {
            return false;
        } else {
            MethodKey other = (MethodKey) o;

            if (this.targetClass == other.targetClass) {
                if (method.getName().equals(other.method.getName())
                        && method.getReturnType().equals(other.method.getReturnType())
                        && ClassUtil.equalParamTypes(method.getParameterTypes(), other.method.getParameterTypes())) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public int hashCode() {
        if (targetClass == null) {
            return method.hashCode();
        } else {
            return targetClass.getName().hashCode() ^ method.getName().hashCode();
        }
    }

    @Override
    public String toString() {
        if (targetClass == null) {
            return method.toString();
        } else {
            return method.toString() + " on " + targetClass.getName();
        }
    }

    @Override
    public int compareTo(MethodKey o) {
        int result = this.method.getName().compareTo(o.method.getName());

        if (result == 0) {
            result = this.method.toString().compareTo(o.method.toString());
            if (result == 0 && this.targetClass != null && o.targetClass != null) {
                result = this.targetClass.getName().compareTo(o.targetClass.getName());
            }
        }

        return result;
    }
}