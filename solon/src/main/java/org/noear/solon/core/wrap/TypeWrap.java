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
package org.noear.solon.core.wrap;

import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.lang.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * 类型包装（泛型估审包装）
 *
 * @author noear
 * @since 3.0
 */
public class TypeWrap {
    private Class<?> type;
    private ParameterizedType genericType;
    private boolean invalid;

    public TypeWrap(Type genericInfo, Class<?> type, Type genericType) {
        this.type = type;

        if (type == Void.class) {
            //空类型不需要处理
            return;
        }

        Type tmp = GenericUtil.reviewType(genericType, genericInfo);

        if (tmp instanceof ParameterizedType) {
            this.genericType = (ParameterizedType) tmp;
            tmp = this.genericType.getRawType();

            if (tmp instanceof Class<?>) {
                this.type = (Class<?>) tmp;
            } else {
                //说明解码失败了
                this.type = Object.class;
                //this.invalid = true;
            }
        } else if (tmp instanceof TypeVariable) {
            //说明解码失败了
            this.type = Object.class;
            //this.invalid = true;
        } else if (tmp instanceof Class<?>) {
            //如果原来是 TypeVariable，会被转成正常类型
            this.type = (Class<?>) tmp;
        }
    }

    /**
     * 是否无效
     */
    public boolean isInvalid() {
        return invalid;
    }

    /**
     * 获取类型
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * 获取泛型
     */
    public @Nullable ParameterizedType getGenericType() {
        return genericType;
    }
}