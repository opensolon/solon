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
package org.noear.solon.core.route;

import org.noear.solon.core.handle.MethodType;

/**
 * 路由记录
 *
 * @author noear
 * @since 1.3
 */
public interface Routing<T> {
    /**
     * 顺序位
     */
    int index();

    /**
     * 路径
     */
    String path();

    /**
     * 方法
     */
    MethodType method();

    /**
     * 路由目标
     */
    T target();

    /**
     * 是否匹配
     */
    boolean matches(MethodType method2, String path2);

    /**
     * 匹配程度（0,不匹配；1,匹配路径；2,完全匹配）
     *
     * @since 2.5
     */
    int degrees(MethodType method2, String path2);


    /**
     * 测试路径
     *
     * @since 2.6
     */
    boolean test(String path2);
}
