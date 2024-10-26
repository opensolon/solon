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
package org.noear.solon.data.sqlink.base.toBean.handler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeRef<T>
{
    private final Type actualType;

    public Type getActualType()
    {
        return actualType;
    }

    public TypeRef()
    {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType)
        {
            ParameterizedType superclass = (ParameterizedType) genericSuperclass;
            actualType = superclass.getActualTypeArguments()[0];
        }
        else
        {
            throw new RuntimeException("TypeRef<T>需要一个提供了具体类型参数的类或匿名类");
        }
    }
}
