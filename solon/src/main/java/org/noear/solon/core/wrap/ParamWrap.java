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

import java.lang.reflect.*;

/**
 * 参数包装
 *
 * @author noear
 * @since 1.2
 * @since 1.6
 * @since 2.4
 * @since 3.0
 */
public class ParamWrap extends VarDescriptorBase {
    private final Parameter parameter;
    private Class<?> type;
    private @Nullable ParameterizedType genericType;

    public ParamWrap(Parameter parameter) {
        this(parameter, null, null);
    }

    public ParamWrap(Parameter parameter, Executable method, Class<?> clz) {
        super(parameter, parameter.getName());
        this.parameter = parameter;
        this.type = parameter.getType();

        Type type0 = parameter.getParameterizedType();

        if (method != null) {
            //for action
            this.init();
        }

        if (clz != null) {
            //@since 3.0
            if (type0 instanceof TypeVariable || type0 instanceof ParameterizedType) {
                type0 = GenericUtil.reviewType(type0, GenericUtil.getGenericInfo(clz));

                if (type0 instanceof ParameterizedType) {
                    genericType = (ParameterizedType) type0;
                    type = (Class<?>) genericType.getRawType();
                } else if (type0 instanceof Class) {
                    genericType = null;
                    type = (Class<?>) type0;
                } else {
                    throw new IllegalStateException("Method parameter generic analysis failed: "
                            + method.getDeclaringClass().getName()
                            + "."
                            + method.getName());
                }
            }
        }

        if (genericType == null && type0 instanceof ParameterizedType) {
            genericType = (ParameterizedType) type0;
        }
    }

    /**
     * 获取原始参数
     */
    public Parameter getParameter() {
        return parameter;
    }

    /**
     * 获取泛型
     */
    @Override
    public Type getGenericType() {
        return genericType;
    }

    /**
     * 获取类型
     */
    @Override
    public Class<?> getType() {
        return type;
    }
}