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
package org.noear.solon.test.data;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.util.RunnableEx;
import org.noear.solon.data.annotation.TranAnno;
import org.noear.solon.data.tran.TranUtils;
import org.noear.solon.test.annotation.Rollback;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 回滚拦截器
 *
 * @author noear
 * @since 1.10
 */
public class RollbackInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        if (Solon.app() == null) {
            //没有容器，没法运行事务回滚
            return inv.invoke();
        } else {
            AtomicReference valRef = new AtomicReference();

            //尝试找函数上的
            Rollback anno = inv.getMethodAnnotation(Rollback.class);

            //尝试找类上的
            if (anno == null) {
                anno = inv.getTargetAnnotation(Rollback.class);
            }

            if (anno == null || anno.value() == false) {
                //如果没有注解，或者不需要强制回滚
                return inv.invoke();
            } else {
                //如果需要强制回滚
                rollbackDo(() -> {
                    valRef.set(inv.invoke());
                });

                return valRef.get();
            }
        }
    }

    /**
     * 回滚事务
     */
    public static void rollbackDo(RunnableEx runnable) throws Throwable {
        try {
            //应用 //添加路由拦截器（放到最里层）
            Solon.app().chainManager().addInterceptorIfAbsent(RollbackRouterInterceptor.getInstance(), Integer.MAX_VALUE);

            //当前
            TranUtils.execute(new TranAnno(), () -> {
                runnable.run();
                throw new RollbackException();
            });
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            if (e instanceof RollbackException) {
                System.out.println("@Rollback: the transaction has been rolled back!");
            } else {
                throw e;
            }
        } finally {
            //应用 //移除路由拦截器（恢复原状）
            Solon.app().chainManager().removeInterceptor(RollbackRouterInterceptor.class);
        }
    }
}
