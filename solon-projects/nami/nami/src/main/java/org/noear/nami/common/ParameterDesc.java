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

import org.noear.nami.annotation.NamiBody;
import org.noear.nami.annotation.NamiParam;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Cookie;
import org.noear.solon.annotation.Header;
import org.noear.solon.annotation.Param;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * 参数描述
 *
 * @author noear
 * @since 3.2
 */
public class ParameterDesc {
    private final Parameter parameter;
    private String name;
    private boolean isHeader;
    private boolean isCookie;
    private boolean isBody;

    public ParameterDesc(Parameter parameter) {
        this.parameter = parameter;

        this.resolveAnno();

        if (TextUtils.isEmpty(name)) {
            name = parameter.getName();
        }
    }

    /**
     * 分析注解
     */
    private void resolveAnno() {
        NamiBody namiBodyAnno = parameter.getAnnotation(NamiBody.class);
        if (namiBodyAnno != null) {
            isBody = true;
            return;
        }

        NamiParam namiParamAnno = parameter.getAnnotation(NamiParam.class);
        if (namiParamAnno != null) {
            name = namiParamAnno.value();
            return;
        }

        Body bodyAnno = parameter.getAnnotation(Body.class);
        if (bodyAnno != null) {
            isBody = true;
            return;
        }

        Param paramAnno = parameter.getAnnotation(Param.class);
        if (paramAnno != null) {
            name = Utils.annoAlias(paramAnno.value(), paramAnno.name());
            return;
        }


        Header headerAnno = parameter.getAnnotation(Header.class);
        if (headerAnno != null) {
            isHeader = true;
            name = Utils.annoAlias(headerAnno.value(), headerAnno.name());
            return;
        }

        Cookie cookieAnno = parameter.getAnnotation(Cookie.class);
        if (cookieAnno != null) {
            isCookie = true;
            name = Utils.annoAlias(cookieAnno.value(), cookieAnno.name());
            return;
        }
    }

    /**
     * 是否为主体
     */
    public boolean isBody() {
        return isBody;
    }

    /**
     * 是否为头
     */
    public boolean isHeader() {
        return isHeader;
    }

    /**
     * 是否为小饼
     */
    public boolean isCookie() {
        return isCookie;
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