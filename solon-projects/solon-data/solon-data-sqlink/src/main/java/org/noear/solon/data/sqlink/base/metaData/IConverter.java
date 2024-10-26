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
package org.noear.solon.data.sqlink.base.metaData;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface IConverter<J, D>
{
    D toDb(J value, PropertyMetaData propertyMetaData);

    J toJava(D value, PropertyMetaData propertyMetaData);

    default Class<D> getDbType()
    {
        Type[] interfaces = this.getClass().getGenericInterfaces();
        Type type = interfaces[0];
        if (type instanceof ParameterizedType)
        {
            ParameterizedType pType = (ParameterizedType) type;
            Type dbType = pType.getActualTypeArguments()[1];
            return (Class<D>) dbType;
        }
        else
        {
            throw new RuntimeException(String.format("无法找到%s的第%s个泛型类型", type, 1));
        }
    }

    default Class<J> getJavaType()
    {
        Type[] interfaces = this.getClass().getGenericInterfaces();
        Type type = interfaces[0];
        if (type instanceof ParameterizedType)
        {
            ParameterizedType pType = (ParameterizedType) type;
            Type dbType = pType.getActualTypeArguments()[0];
            return (Class<J>) dbType;
        }
        else
        {
            throw new RuntimeException(String.format("无法找到%s的第%s个泛型类型", type, 10));
        }
    }
}
