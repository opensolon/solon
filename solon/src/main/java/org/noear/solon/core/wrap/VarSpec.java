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
package org.noear.solon.core.wrap;

import org.noear.solon.core.handle.Context;
import org.noear.solon.lang.Nullable;

import java.lang.reflect.ParameterizedType;

/**
 * 变量说明
 *
 * @author noear
 * @since 2.3
 */
public interface VarSpec {
    /**
     * 必须有 body（一般是指用了 @Body 注解）
     */
    boolean isRequiredBody();

    /**
     * 必须有 header（一般是指用了 @Header 注解）
     */
    boolean isRequiredHeader();

    /**
     * 必须有 cookie（一般是指用了 @Cookie 注解）
     */
    boolean isRequiredCookie();

    /**
     * 必须有 path（一般是指用了 @Path 注解）
     */
    boolean isRequiredPath();

    /**
     * 必须有输入（一般是指注解里 required = true）
     */
    boolean isRequiredInput();

    /**
     * 获取必须缺失时的提示
     */
    String getRequiredHint();

    /**
     * 获取名字
     */
    String getName();

    /**
     * 获取默认值
     * */
    String getDefaultValue();

    /**
     * 获取类型
     * */
    Class<?> getType();

    /**
     * 获取泛型
     * */
    @Nullable
    ParameterizedType getGenericType();

    /**
     * 是否为泛型
     * */
    default boolean isGenericType(){
        return getGenericType() instanceof ParameterizedType;
    }


    /**
     * 获取参数值
     */
    default String getValue(Context ctx) {
        if (isRequiredHeader()) {
            return ctx.header(getName());
        } else if (isRequiredCookie()) {
            return ctx.cookie(getName());
        } else {
            return ctx.param(getName());
        }
    }

    /**
     * 获取值
     * */
    default String[] getValues(Context ctx) {
        if (isRequiredHeader()) {
            return ctx.headerValues(getName());
        } else if (isRequiredCookie()) {
            return ctx.cookieValues(getName());
        } else {
            return ctx.paramValues(getName());
        }
    }
}
