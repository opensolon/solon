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

import javax.servlet.ServletResponse; //用于检测

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.view.jsp.JspRender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewJspPlugin implements Plugin {
    static final Logger log = LoggerFactory.getLogger(ViewJspPlugin.class);

    @Override
    public void start(AppContext context) {
        if (ClassUtil.hasClass(() -> ServletResponse.class) == false) {
            log.warn("View: javax.servlet.ServletResponse not exists! JspRender failed to load.");
            return;
        }

        JspRender render = new JspRender();

        context.app().renders().register(null, render);
        context.app().renders().register(".jsp", render);
    }
}