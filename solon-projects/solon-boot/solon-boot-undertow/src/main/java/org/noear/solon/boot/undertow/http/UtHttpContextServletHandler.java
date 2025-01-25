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
package org.noear.solon.boot.undertow.http;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.web.servlet.SolonServletHandler;
import org.noear.solon.boot.undertow.integration.UndertowPlugin;
import org.noear.solon.core.handle.Context;

import java.io.IOException;

//Servlet模式
public class UtHttpContextServletHandler extends SolonServletHandler {
    @Override
    protected void preHandle(Context ctx) throws IOException {
        if (ServerProps.output_meta) {
            ctx.headerSet("Solon-Boot", UndertowPlugin.solon_boot_ver());
        }
    }
}