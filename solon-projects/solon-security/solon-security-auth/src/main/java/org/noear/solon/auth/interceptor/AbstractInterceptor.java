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
package org.noear.solon.auth.interceptor;

import org.noear.solon.auth.AuthException;
import org.noear.solon.auth.AuthStatus;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.aspect.MethodInterceptor;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.util.DataThrowable;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 1.3
 */
public abstract class AbstractInterceptor<T extends Annotation> implements MethodInterceptor {

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        T anno = inv.getMethodAnnotation(type());

        if (anno == null) {
            anno = inv.getTargetAnnotation(type());
        }

        if (anno != null) {
            Result rst = verify(anno, inv);

            if (rst.getCode() != Result.SUCCEED_CODE) {
                Context ctx = Context.current();

                if (ctx != null) {
                    ctx.setHandled(true);
                    ctx.setRendered(true);
                    AuthUtil.adapter().failure().onFailure(ctx, rst);
                    throw new DataThrowable();
                } else {
                    throw new AuthException((AuthStatus) rst.getData(), rst.getDescription());
                }
            }
        }

        return inv.invoke();
    }


    /**
     * 注解类型
     */
    public abstract Class<T> type();

    /**
     * 验证
     */
    public abstract Result verify(T anno, Invocation inv) throws Exception;
}
