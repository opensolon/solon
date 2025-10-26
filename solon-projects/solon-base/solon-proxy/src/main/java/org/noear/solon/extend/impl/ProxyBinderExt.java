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
package org.noear.solon.extend.impl;

import org.noear.eggg.ClassEggg;
import org.noear.eggg.MethodEggg;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ProxyBinder;
import org.noear.solon.proxy.BeanProxy;

import java.lang.reflect.Modifier;

/**
 * @author noear
 * @since 2.5
 */
public class ProxyBinderExt extends ProxyBinder {
    @Override
    public void binding(BeanWrap bw) {
        if (bw.clz().isInterface()) {
            throw new IllegalStateException("Interfaces are not supported as proxy components: " + bw.clz().getName());
        }

        int modifier = bw.clz().getModifiers();
        if (Modifier.isFinal(modifier)) {
            throw new IllegalStateException("Final classes are not supported as proxy components: " + bw.clz().getName());
        }

        if (Modifier.isAbstract(modifier)) {
            throw new IllegalStateException("Abstract classes are not supported as proxy components: " + bw.clz().getName());
        }

        if (Modifier.isPublic(modifier) == false) {
            throw new IllegalStateException("Not public classes are not supported as proxy components: " + bw.clz().getName());
        }

        if (NativeDetector.isAotRuntime()) {
            //如果是 aot 则注册函数
            ClassEggg rawEggg = bw.rawEggg();
            for (MethodEggg me : rawEggg.getOwnMethodEgggs()) {
                bw.context().aot().registerMethodEggg(me);
            }
        }

        bw.proxySet(BeanProxy.getGlobal());
    }
}