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

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

/**
 * 构造器包装
 *
 * @author noear
 * @since 3.0
 */
public class ConstructorWrap {
    private final Class<?> ownerClz;
    private final Constructor constructor;
    private final ParamWrap[] paramWraps;

    public ConstructorWrap(Class<?> clz, Constructor constructor) {
        this.ownerClz = clz;
        this.constructor = constructor;
        this.paramWraps = buildParamWraps(constructor.getParameters(), clz);
    }

    /**
     * 获取所有者类
     */
    public Class<?> getOwnerClz() {
        return ownerClz;
    }

    /**
     * 获取原始构造器
     */
    public Constructor getConstructor() {
        return constructor;
    }

    /**
     * 获取参数包装
     */
    public ParamWrap[] getParamWraps() {
        return paramWraps;
    }

    private ParamWrap[] buildParamWraps(Parameter[] pAry, Class<?> clz) {
        ParamWrap[] tmp = new ParamWrap[pAry.length];
        for (int i = 0, len = pAry.length; i < len; i++) {
            //@since 3.0
            tmp[i] = new ParamWrap(pAry[i], constructor, clz);
        }

        return tmp;
    }
}