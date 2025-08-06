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
package org.noear.solon.data.util;

import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.util.TmplUtil;

import java.lang.reflect.Method;

/**
 * 拦截动作模板处理
 *
 * @author noear
 * @since 1.6
 */
public class InvKeys {

    /**
     * 基于调用构建Key
     *
     * @param inv 拦截动作
     */
    public static String buildByInv(Invocation inv) {
        Method method = inv.method().getMethod();

        StringBuilder keyB = new StringBuilder();

        keyB.append(method.getDeclaringClass().getName()).append(":");
        keyB.append(method.getName()).append(":");

        inv.argsAsMap().forEach((k, v) -> {
            keyB.append(k).append("_").append(v);
        });

        //必须md5，不然会出现特殊符号
        return Utils.md5(keyB.toString());
    }

    /**
     * 基于模板与调用构建Key
     *
     * @param tml 模板
     * @param inv 拦截动作
     */
    public static String buildByTmlAndInv(String tml, Invocation inv) {
        return TmplUtil.parse(tml, inv, null);
    }

    /**
     * 基于模板与调用构建Key
     *
     * @param tml 模板
     * @param inv 拦截动作
     * @param rst 返回值
     */
    public static String buildByTmlAndInv(String tml, Invocation inv, Object rst) {
        return TmplUtil.parse(tml, inv, rst);
    }
}
