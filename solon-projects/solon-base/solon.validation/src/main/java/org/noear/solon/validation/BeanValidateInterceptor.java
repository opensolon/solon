/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.validation;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Result;

/**
 * 实体验证拦截器
 *
 * @author noear
 * @since 1.3
 */
public class BeanValidateInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        //内部会出示异常
        try {
            ValidatorManager.validateOfInvocation(inv);
        } catch (ValidatorException e) {
            String msg = inv.method().getMethod() + " valid failed: " + e.getMessage();
            throw new ValidatorException(e.getCode(), msg, e.getAnnotation(), e.getResult(), inv.method().getMethod());
        } catch (Throwable e) {
            String msg = inv.method().getMethod() + " valid failed: " + e.getMessage();
            throw new ValidatorException(400, msg, null, Result.failure(), inv.method().getMethod());
        }

        return inv.invoke();
    }
}