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
package org.noear.nami.common;

import org.noear.nami.annotation.NamiParam;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Param;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * 参数包装器
 *
 * @author noear
 * @since 3.2
 */
public class ParameterWrap {
    private final Parameter parameter;
    private String name;

    public ParameterWrap(Parameter parameter) {
        this.parameter = parameter;

        NamiParam namiParamAnno = parameter.getAnnotation(NamiParam.class);
        if (namiParamAnno != null) {
            name = namiParamAnno.value();
        } else {
            Param paramAnno = parameter.getAnnotation(Param.class);
            if (paramAnno != null) {
                name = Utils.annoAlias(paramAnno.value(), paramAnno.name());
            }
        }

        if (TextUtils.isEmpty(name)) {
            name = parameter.getName();
        }
    }

    /**
     * 获取类型
     */
    public Type getType() {
        return parameter.getType();
    }

    /**
     * 获取名字
     */
    public String getName() {
        return name;
    }
}