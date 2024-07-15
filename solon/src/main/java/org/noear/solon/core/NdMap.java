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
package org.noear.solon.core;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.IgnoreCaseMap;

import java.util.Map;

/**
 * 可排序，不区分大小写（Name data map）
 *
 * 用于：Attr 处理
 *
 * @see Context#attrMap()
 * @author noear
 * @since 1.3
 * @removal true
 * */
@Deprecated
public class NdMap extends IgnoreCaseMap<Object> {

    public NdMap() {
        super();
    }

    public NdMap(Map map) {
        super();
        map.forEach((k, v) -> {
            put(k.toString(), v);
        });
    }
}
