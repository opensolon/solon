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
package org.noear.solon.auth.integration;

import org.noear.solon.auth.AuthAdapter;
import org.noear.solon.auth.AuthAdapterSupplier;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.*;
import org.noear.solon.auth.interceptor.*;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        context.beanInterceptorAdd(AuthIp.class, new IpInterceptor());
        context.beanInterceptorAdd(AuthLogined.class, new LoginedInterceptor());
        context.beanInterceptorAdd(AuthPath.class, new PathInterceptor());
        context.beanInterceptorAdd(AuthPermissions.class, new PermissionsInterceptor());
        context.beanInterceptorAdd(AuthRoles.class, new RolesInterceptor());

        context.subBeansOfType(AuthAdapter.class, e -> AuthUtil.adapterAdd(e));

        //@deprecated 3.0
        context.subBeansOfType(AuthAdapterSupplier.class, e -> AuthUtil.adapterAdd(e));
    }
}