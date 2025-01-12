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
package com.layjava.docs.javadoc.solon.impl;

import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.layjava.docs.javadoc.solon.wrap.ApiImplicitParamImpl;
import com.layjava.docs.javadoc.solon.wrap.ApiParamAnno;
import com.layjava.docs.javadoc.solon.wrap.ApiParamImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.docs.ApiEnum;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * @author noear
 * @since 2.3
 */
public class ParamHolder {
    private ParamWrap param;
    private ApiParamAnno anno;
    private MethodJavadoc methodJavadoc;

    public ParamHolder(ParamWrap param) {
        this.param = param;
        if (param != null) {
            ApiParam tmp = param.getParameter().getAnnotation(ApiParam.class);
            if (tmp != null) {
                anno = new ApiParamImpl(tmp);
            }
        }
    }

    public ParamHolder(ParamWrap param, MethodJavadoc methodJavadoc) {
        this.param = param;
        this.methodJavadoc = methodJavadoc;
        if (param != null) {
            ApiParam tmp = param.getParameter().getAnnotation(ApiParam.class);
            if (tmp != null) {
                anno = new ApiParamImpl(tmp);
            }
        }
    }

    public ParamHolder binding(ApiImplicitParamImpl anno) {
        if (anno != null) {
            this.anno = anno;
        }

        return this;
    }

    @Override
    public String toString() {
        return getName();
    }

    public ParamWrap getParam() {
        return param;
    }

    public ApiImplicitParam getAnno() {
        return anno;
    }

    /**
     * 名字
     */
    public String getName() {
        if (param != null) {
            return param.spec().getName();
        }

        if (anno != null) {
            return anno.name();
        }

        return null;
    }

    /**
     * 描述
     */
    public String getDescription() {
        if (anno != null) {
            return anno.value();
        }

        return null;
    }

    public boolean isMap() {
        if (param != null) {
            return Map.class.isAssignableFrom(param.getType());
        }

        return false;
    }

    public boolean isArray() {
        if (param != null) {
            return Collection.class.isAssignableFrom(param.getType());
        }

        return false;
    }

    /**
     * 获取数据类型
     */
    public String dataType() {
        if (param != null) {
            if (UploadedFile.class.equals(param.getType())) {
                return ApiEnum.FILE;
            }

            return param.getType().getSimpleName();
        }

        String tmp = null;
        if (anno != null) {
            tmp = anno.dataType();

            if (Utils.isEmpty(tmp) && anno.dataTypeClass() != Void.class) {
                tmp = anno.dataTypeClass().getSimpleName();
            }
        }

        if (Utils.isBlank(tmp)) {
            return ApiEnum.STRING;
        } else {
            return tmp;
        }
    }

    public Class<?> dataTypeClass() {
        if (param != null) {
            return param.getType();
        }

        if (anno != null) {
            return anno.dataTypeClass();
        }

        return null;
    }

    public Type dataGenericType() {
        if (param != null) {
            return param.getGenericType();
        }

        return null;
    }

    public String paramType() {
        if (param != null) {
            if (param.spec().isRequiredBody()) {
                return ApiEnum.PARAM_TYPE_BODY;
            }
        }

        String tmp = null;
        if (anno != null) {
            tmp = anno.paramType();
        }

        if (Utils.isBlank(tmp)) {
            return ApiEnum.PARAM_TYPE_QUERY;
        } else {
            return tmp;
        }
    }

    public boolean allowMultiple() {
        if (param != null) {
            return param.getType().isArray() ||
                    Collection.class.isAssignableFrom(param.getType());
        }

        if (anno != null) {
            return anno.allowMultiple();
        }

        return false;
    }

    public boolean hidden() {
        if (anno != null) {
            return anno.hidden();
        }

        return false;
    }

    public boolean isRequired() {
        if (param != null) {
            if (param.spec().isRequiredInput()) {
                return true;
            }
        }

        if (anno != null) {
            return anno.required();
        }

        return false;
    }

    public boolean isRequiredBody() {
        boolean tmp = false;
        if (param != null) {
            tmp = param.spec().isRequiredBody();
        }

        if (!tmp && anno != null) {
            tmp = ApiEnum.PARAM_TYPE_BODY.equals(anno.paramType());
        }

        return tmp;
    }

    public boolean isRequiredHeader() {
        boolean tmp = false;
        if (param != null) {
            tmp = param.spec().isRequiredHeader();
        }

        if (!tmp && anno != null) {
            tmp = ApiEnum.PARAM_TYPE_HEADER.equals(anno.paramType());
        }

        return tmp;
    }

    public boolean isRequiredCookie() {
        boolean tmp = false;
        if (param != null) {
            return param.spec().isRequiredCookie();
        }

        if (!tmp && anno != null) {
            tmp = ApiEnum.PARAM_TYPE_COOKIE.equals(anno.paramType());
        }

        return tmp;
    }

    public boolean isRequiredPath() {
        boolean tmp = false;
        if (param != null) {
            return param.spec().isRequiredPath();
        }

        if (!tmp && anno != null) {
            tmp = ApiEnum.PARAM_TYPE_PATH.equals(anno.paramType());
        }

        return tmp;
    }

    public boolean isReadOnly() {
        if (anno != null) {
            return anno.readOnly();
        }

        return false;
    }

    public boolean isIgnore() {
        if (param != null) {
            if (Context.class.equals(param.getType())) {
                return true;
            }

            if (SessionState.class.equals(param.getType())) {
                return true;
            }
        }

        return hidden();
    }
}
