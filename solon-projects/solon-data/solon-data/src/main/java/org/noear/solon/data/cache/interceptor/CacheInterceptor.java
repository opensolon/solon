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
package org.noear.solon.data.cache.interceptor;

import org.noear.solon.Solon;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.cache.CacheExecutorImp;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.core.aspect.Interceptor;

/**
 * 缓存拦截器
 *
 * @author noear
 * @since 1.0
 * */
public class CacheInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        //支持动态开关缓存
        if (Solon.app().enableCaching()) {
            Cache anno = inv.getMethodAnnotation(Cache.class);

            return CacheExecutorImp.global
                    .cache(anno, inv, () -> inv.invoke());
        } else {
            return inv.invoke();
        }
    }
}
