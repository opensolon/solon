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

import org.noear.eggg.*;
import org.noear.solon.core.wrap.FieldSpec;
import org.noear.solon.core.wrap.ParamSpec;
import org.noear.solon.core.wrap.VarSpec;

import java.lang.reflect.*;
import java.util.*;

/**
 *
 * @author noear
 * @since 3.7
 */
public class EgggUtil {
    private static final Eggg eggg = new Eggg()
            .withAliasHandler(EgggUtil::doAliasHandle)
            .withDigestHandler(EgggUtil::doDigestHandle)
            .withReflectHandler(new EgggReflectHandler());

    private static String doAliasHandle(ClassEggg cw, AnnotatedEggg s, String ref) {
        if (s.getDigest() instanceof VarSpec) {
            return s.<VarSpec>getDigest().getName();
        }

        return ref;
    }

    private static Object doDigestHandle(ClassEggg cw, AnnotatedEggg s, Object ref) {
        if (s instanceof FieldEggg) {
            return new FieldSpec((FieldEggg) s);
        } else if (s instanceof ParamEggg) {
            return new ParamSpec((ParamEggg) s);
        }

        return ref;
    }

    public static Map<String, Type> findGenericInfo(Type type, Class<?> declaringClass) {
        return eggg.findGenericInfo(getTypeEggg(type), declaringClass);
    }

    public static List<Type> findGenericList(Type type, Class<?> declaringClass) {
        return eggg.findGenericList(getTypeEggg(type), declaringClass);
    }

    public static TypeEggg getTypeEggg(Type type) {
        return eggg.getTypeEggg(type);
    }

    public static ClassEggg getClassEggg(Type type) {
        return getTypeEggg(type).getClassEggg();
    }


    static class EgggReflectHandler implements ReflectHandler {
        @Override
        public Field[] getDeclaredFields(Class<?> clazz) {
            return ReflectUtil.getDeclaredFields(clazz);
        }

        @Override
        public Method[] getDeclaredMethods(Class<?> clazz) {
            return ReflectUtil.getDeclaredMethods(clazz);
        }

        @Override
        public Method[] getMethods(Class<?> clazz) {
            return ReflectUtil.getMethods(clazz);
        }
    }

}