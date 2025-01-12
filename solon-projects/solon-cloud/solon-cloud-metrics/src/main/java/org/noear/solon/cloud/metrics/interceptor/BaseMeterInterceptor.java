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
package org.noear.solon.cloud.metrics.interceptor;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 度量注解的拦截器基类
 *
 * @author bai
 * @since 2.4
 */
public abstract class BaseMeterInterceptor<T,M> implements Interceptor {
    private final Map<String, M> meterCached = new ConcurrentHashMap<>();

    /**
     * 获取注解
     */
    protected abstract T getAnno(Invocation inv);

    /**
     * 获取注解名字
     */
    protected abstract String getAnnoName(T anno);

    /**
     * 度量
     */
    protected abstract Object metering(Invocation inv, T anno) throws Throwable;

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        //先在 method 找，再尝试在 class 找
        T anno = getAnno(inv);

        // 尝试计数
        if (anno != null) {
            return metering(inv, anno);
        } else {
            return inv.invoke();
        }
    }

    protected M getMeter(String meterName, Supplier<M> supplier) {
        M tmp = meterCached.computeIfAbsent(meterName, k -> supplier.get());

        return tmp;
    }

    protected String getMeterName(Invocation inv, T anno) {
        String meterName = getAnnoName(anno);

        if (Utils.isEmpty(meterName)) {
            meterName = inv.target().getClass().getName() + "::" + inv.method().getMethod().getName();
        }

        return meterName;
    }

    protected Tags getMeterTags(Invocation inv, String[] annoTags) {
        Tags tags = Tags.of(annoTags);

        Context ctx = Context.current();

        if (ctx != null) {
            tags.and(Tag.of("uri", ctx.path()),
                    Tag.of("method", ctx.method()),
                    Tag.of("class", inv.target().getClass().getTypeName()),
                    Tag.of("executable", inv.method().getMethod().getName()));
        } else {
            tags.and(Tag.of("class", inv.target().getClass().getTypeName()),
                    Tag.of("executable", inv.method().getMethod().getName()));
        }

        return tags;
    }
}
