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
package org.noear.solon.server.jetty.http;

import org.noear.solon.server.ServerProps;
import org.noear.solon.server.jetty.integration.JettyPlugin;
import org.noear.solon.web.servlet.SolonServletHandler;
import org.noear.solon.core.handle.Context;

public class JtHttpContextServletHandler extends SolonServletHandler {
    @Override
    protected boolean useLimitStream() {
        // max body size 目前没法生效，所以采用 LimitedInputStream 控制
        return true;
    }

    @Override
    protected void preHandle(Context ctx) {
        if (ServerProps.output_meta) {
            ctx.headerSet("Solon-Server", JettyPlugin.solon_server_ver());
        }
    }
}
