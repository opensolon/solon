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

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;
import org.noear.solon.data.annotation.TranAnno;
import org.noear.solon.data.tran.TranUtils;

/**
 * @author noear
 * @since 2.6
 */
public class RollbackRouterInterceptor implements RouterInterceptor {
    private static RollbackRouterInterceptor instance = new RollbackRouterInterceptor();

    public static RollbackRouterInterceptor getInstance() {
        return instance;
    }

    private RollbackRouterInterceptor(){

    }

    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        TranUtils.execute(new TranAnno(), () -> {
            chain.doIntercept(ctx, mainHandler);
            throw new RollbackException();
        });
    }
}
