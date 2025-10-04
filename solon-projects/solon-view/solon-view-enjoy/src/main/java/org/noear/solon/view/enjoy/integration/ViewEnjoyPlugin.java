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
package org.noear.solon.view.enjoy.integration;

import com.jfinal.template.Directive;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.tags.AuthConstants;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Constants;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.view.enjoy.EnjoyDirectiveFactory;
import org.noear.solon.view.enjoy.EnjoyRender;
import org.noear.solon.view.enjoy.tags.AuthPermissionsTag;
import org.noear.solon.view.enjoy.tags.AuthRolesTag;

public class ViewEnjoyPlugin implements Plugin {
    @Override
    public void start(AppContext context) {
        EnjoyRender render = new EnjoyRender();

        context.app().shared().forEach((k, v) -> {
            render.putVariable(k, v);
        });

        context.app().onSharedAdd((k, v) -> {
            render.putVariable(k, v);
        });

        context.lifecycle(Constants.LF_IDX_PLUGIN_BEAN_USES, () -> {
            context.beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if (Directive.class.isAssignableFrom(v.clz())) {
                        render.putDirective(k.split(":")[1], new EnjoyDirectiveFactory(v));
                    }
                    return;
                }

                if (k.startsWith("share:")) { //java share object
                    render.putVariable(k.split(":")[1], v.raw());
                    return;
                }
            });
        });

        context.app().renders().register(render);
        context.wrapAndPut(EnjoyRender.class, render); //用于扩展

        if (ClassUtil.hasClass(() -> AuthUtil.class)) {
            render.putDirective(AuthConstants.TAG_authPermissions, AuthPermissionsTag.class);
            render.putDirective(AuthConstants.TAG_authRoles, AuthRolesTag.class);
        }
    }
}
