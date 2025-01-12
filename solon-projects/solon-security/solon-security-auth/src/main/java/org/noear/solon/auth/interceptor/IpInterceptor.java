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
import org.noear.solon.auth.annotation.AuthIp;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

/**
 * AuthIp 注解拦截器
 *
 * @author noear
 * @since 1.4
 */
public class IpInterceptor extends AbstractInterceptor<AuthIp> {
    @Override
    public Class<AuthIp> type() {
        return AuthIp.class;
    }

    @Override
    public Result verify(AuthIp anno, Invocation inv) throws Exception {
        Context ctx = Context.current();

        if (ctx == null) {
            return Result.succeed();
        } else {
            String ip = ctx.realIp();

            if (AuthUtil.verifyIp(ip)) {
                return Result.succeed();
            } else {
                return AuthStatus.OF_IP.toResult(ip);
            }
        }
    }
}
