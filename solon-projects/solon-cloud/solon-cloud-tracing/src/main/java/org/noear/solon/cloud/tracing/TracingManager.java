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
package org.noear.solon.cloud.tracing;

import io.opentracing.Tracer;
import org.noear.nami.NamiManager;
import org.noear.solon.Solon;
import org.noear.solon.cloud.tracing.annotation.Tracing;
import org.noear.solon.cloud.tracing.annotation.TracingInterceptor;
import org.noear.solon.cloud.tracing.integration.NamiFilterTracing;
import org.noear.solon.cloud.tracing.integration.SolonFilterTracing;
import org.noear.solon.cloud.tracing.service.TracerFactory;
import org.noear.solon.core.util.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 跟踪管理器
 *
 * @author noear
 * @since 1.7
 */
public class TracingManager {
    private static AtomicBoolean enabled = new AtomicBoolean(false);

    /**
     * 启用
     */
    public static void enable(String excluded) {
        if (enabled.compareAndSet(false, true)) {
            //添加 nami 适配
            NamiManager.reg(new NamiFilterTracing());

            //添加 solon 适配
            Solon.app().filter(new SolonFilterTracing(excluded));

            //添加 @Tracing 适配
            Solon.context().beanInterceptorAdd(Tracing.class, new TracingInterceptor());
        }
    }

    /**
     * 注册
     */
    public static void register(TracerFactory service) {
        try {
            Solon.context().wrapAndPut(Tracer.class, service.create());

            LogUtil.global().info("Cloud: TracerFactory registered from the " + service.getClass().getTypeName());
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
