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
package org.noear.solon.auth;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

/**
 * 认证失败处理者-默认实现
 *
 * @author noear
 * @since 1.10
 */
public class AuthFailureHandlerDefault implements AuthFailureHandler {
    @Override
    public void onFailure(Context ctx, Result rst) throws Throwable {
        throw new AuthException((AuthStatus) rst.getData(), rst.getDescription());
    }
}
