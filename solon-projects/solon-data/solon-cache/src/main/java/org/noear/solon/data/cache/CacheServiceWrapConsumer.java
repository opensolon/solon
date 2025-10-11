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
package org.noear.solon.data.cache;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;

import java.util.function.Consumer;

/**
 * 缓存服务事件监控器。监听BeanWrap，获得CacheService bean
 *
 * @author noear
 * @since 1.0
 * */
public class CacheServiceWrapConsumer implements Consumer<BeanWrap> {
    @Override
    public void accept(BeanWrap bw) {
        if (Utils.isEmpty(bw.name())) {
            CacheLib.cacheServiceAdd("", bw.raw());
        } else {
            CacheLib.cacheServiceAddIfAbsent(bw.name(), bw.raw());

            if (bw.typed()) {
                CacheLib.cacheServiceAdd("", bw.raw());
            }
        }
    }
}
