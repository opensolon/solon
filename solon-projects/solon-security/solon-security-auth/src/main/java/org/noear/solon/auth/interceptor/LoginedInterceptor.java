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

import org.noear.solon.auth.AuthStatus;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.AuthLogined;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Result;

/**
 * AuthLogined 注解拦截器
 *
 * @author noear
 * @since 1.3
 */
public class LoginedInterceptor extends AbstractInterceptor<AuthLogined> {
    @Override
    public Class<AuthLogined> type() {
        return AuthLogined.class;
    }

    @Override
    public Result verify(AuthLogined anno, Invocation inv) throws Exception {
        if (AuthUtil.verifyLogined()) {
            return Result.succeed();
        } else {
            return AuthStatus.OF_LOGINED.toResult();
        }
    }
}
