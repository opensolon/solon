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
package org.noear.solon.web.servlet;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.MimeType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet Handler，适配为 Solon 入口
 *
 * @author noear
 * @since 1.2
 * */
public class SolonServletHandler extends HttpServlet {

    protected void preHandle(Context ctx) throws IOException{

    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SolonServletContext ctx = new SolonServletContext(request, response);
        ctx.contentType(MimeType.TEXT_PLAIN_UTF8_VALUE);

        preHandle(ctx);

        Solon.app().tryHandle(ctx);

        if(ctx.asyncStarted() == false){
            ctx.innerCommit();
        }
    }
}
