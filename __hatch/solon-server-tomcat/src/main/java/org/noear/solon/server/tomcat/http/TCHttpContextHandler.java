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
package org.noear.solon.server.tomcat.http;

import org.noear.solon.server.ServerProps;
import org.noear.solon.server.tomcat.XPluginImp;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.servlet.SolonServletHandler;

import javax.servlet.annotation.WebServlet;

//Servlet模式 注解用于JSP混合模式的搭建
@WebServlet(
        name = "solon",
        urlPatterns = {"/"}
)
public class TCHttpContextHandler extends SolonServletHandler {
    @Override
    protected void preHandle(Context ctx) {
        if (ServerProps.output_meta) {
            ctx.headerSet("Solon-Server", XPluginImp.solon_server_ver());
        }
    }
}