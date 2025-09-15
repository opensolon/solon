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
package org.noear.solon.data.tran.interceptor;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.aspect.MethodInterceptor;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.tran.TranUtils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 事务拦截器
 *
 * @author noear
 * @since 1.0
 * @deprecated 3.1 {@link TransactionInterceptor}
 * */
@Deprecated
public class TranInterceptor implements MethodInterceptor {
    public static final TranInterceptor instance = new TranInterceptor();

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        //支持动态开关事务
        if (inv.context().app().enableTransaction()) {
            Tran anno = inv.getMethodAnnotation(Tran.class);
            if (anno == null) {
                anno = inv.getTargetAnnotation(Tran.class);
            }

            if (anno == null) {
                return inv.invoke();
            } else {
                AtomicReference val0 = new AtomicReference();
                Tran anno0 = anno;

                TranUtils.execute(anno0, () -> {
                    val0.set(inv.invoke());
                });

                return val0.get();
            }
        } else {
            return inv.invoke();
        }
    }
}
