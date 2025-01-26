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
package org.noear.solon.view.jsp.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.view.jsp.JspRender;

public class ViewJspPlugin implements Plugin {

    @Override
    public void start(AppContext context) {
        if (ClassUtil.loadClass("javax.servlet.ServletResponse") == null) {
            LogUtil.global().warn("View: javax.servlet.ServletResponse not exists! JspRender failed to load.");
            return;
        }

        JspRender render = new JspRender();

        context.app().renderManager().register(null, render);
        context.app().renderManager().register(".jsp", render);
    }
}