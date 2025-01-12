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
package org.noear.solon.aot.proxy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import org.noear.solon.core.util.GenericUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类名称工具
 *
 * @author noear
 * @since 2.2
 */
public class TypeNameUtil {
    /**
     * 获取类名称（支持泛型）
     */
    public static TypeName getTypeName(Map<String, Type> typeGenericMap, Class<?> clz, Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType type2 = (ParameterizedType) type;

            if (type2.getActualTypeArguments().length > 0) {
                //支持泛型
                ClassName petClassName = ClassName.get(clz);
                List<TypeName> petTypeVars = new ArrayList<>();
                for (Type e : type2.getActualTypeArguments()) {
                    e = GenericUtil.reviewType(e, typeGenericMap);
                    petTypeVars.add(TypeName.get(e));
                }

                return ParameterizedTypeName.get(petClassName, petTypeVars.toArray(new TypeName[]{}));
            } else {
                return TypeName.get(type);
            }
        } else {
            if (type instanceof TypeVariable) {
                Type type2 = typeGenericMap.get(type.getTypeName());

                if (type2 != null) {
                    type = type2;
                } else {
                    return getTypeVariableName(typeGenericMap, (TypeVariable) type);
                }
            }

            return TypeName.get(type);
        }
    }

    public static TypeVariableName getTypeVariableName(Map<String, Type> typeGenericMap, TypeVariable type) {
        if (type.getBounds().length > 0) {
            Type[] bounds = new Type[type.getBounds().length];

            for (int i = 0; i < bounds.length; i++) {
                Type b1 = type.getBounds()[i];
                bounds[i] = GenericUtil.reviewType(b1, typeGenericMap);
            }

            return TypeVariableName.get(type.getTypeName(), bounds);
        } else {
            return TypeVariableName.get(type);
        }
    }

//    public static Type getType(Map<String, Type> typeGenericMap, Class<?> clz, Type type) {
//        return GenericUtil.reviewType(type, typeGenericMap);
//    }
}
