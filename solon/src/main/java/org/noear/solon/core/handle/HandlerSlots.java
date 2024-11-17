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
package org.noear.solon.core.handle;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Mapping;

import java.util.Set;

/**
 * 通用处理接口接收槽
 *
 * @author noear
 * @since 1.0
 * @since 3.0
 * */
public interface HandlerSlots {
    /**
     * 添加主体处理
     */
    void add(String expr, MethodType method, int index, Handler handler);

    /**
     * 添加主体处理
     */
    default void add(String expr, MethodType method, Handler handler) {
        add(expr, method, 0, handler);
    }

    /**
     * 添加主体处理
     */
    default void add(Mapping mapping, Set<MethodType> methodTypes, int index, Handler handler) {
        String path = Utils.annoAlias(mapping.value(), mapping.path());

        for (MethodType m1 : methodTypes) {
            add(path, m1, index, handler);
        }
    }
}
