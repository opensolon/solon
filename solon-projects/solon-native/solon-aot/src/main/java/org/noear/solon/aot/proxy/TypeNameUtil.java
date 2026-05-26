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
import org.noear.eggg.MethodEggg;
import org.noear.eggg.TypeEggg;
import org.noear.solon.core.util.EgggUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

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
    public static TypeName getTypeName(MethodEggg methodEggg, TypeEggg typeEggg) {
        if (typeEggg.isParameterizedType()) {
            ParameterizedType type2 = typeEggg.getParameterizedType();

            if (type2.getActualTypeArguments().length > 0) {
                //支持泛型
                ClassName petClassName = ClassName.get(typeEggg.getType());
                List<TypeName> petTypeVars = new ArrayList<>();
                for (Type e : type2.getActualTypeArguments()) {
                    petTypeVars.add(TypeName.get(e));
                }

                return ParameterizedTypeName.get(petClassName, petTypeVars.toArray(new TypeName[]{}));
            } else {
                return TypeName.get(typeEggg.getType());
            }
        } else {
            if (typeEggg.isTypeVariable()) {
                return getTypeVariableName(methodEggg, typeEggg.getTypeVariable());
            }

            return TypeName.get(typeEggg.getType());
        }
    }

    public static TypeVariableName getTypeVariableName(MethodEggg methodEggg, TypeVariable typeVariable) {
        Type[] boundsOld = typeVariable.getBounds();

        if (boundsOld.length > 0) {
            Type[] boundsNew = new Type[boundsOld.length];

            for (int i = 0; i < boundsOld.length; i++) {
                Type b1 = boundsOld[i];
                boundsNew[i] = methodEggg.substituteType(b1);
            }

            return TypeVariableName.get(typeVariable.getTypeName(), boundsNew);
        }

        return TypeVariableName.get(typeVariable);
    }
}