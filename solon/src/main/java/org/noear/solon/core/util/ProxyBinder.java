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
package org.noear.solon.core.util;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.runtime.NativeDetector;

/**
 * 代理绑定器
 *
 * @author noear
 * @since 2.5
 */
public class ProxyBinder {

    private static ProxyBinder global;
    static {
        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        global = ClassUtil.tryInstance("org.noear.solon.extend.impl.ProxyBinderExt");

        if (global == null) {
            global = new ProxyBinder();
        }
    }

    public static ProxyBinder global() {
        return global;
    }

    /**
     * 绑定代理
     * */
    public void binding(BeanWrap bw){
        if (NativeDetector.isNotAotRuntime()) {
            throw new IllegalStateException("Missing plugin dependency: 'solon.proxy'");
        }
    }
}
