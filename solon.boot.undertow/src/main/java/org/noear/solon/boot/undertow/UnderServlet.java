/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.noear.solon.boot.undertow;



import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import org.noear.solon.boot.undertow.context.UtHttpServletContext;
import org.noear.solon.boot.undertow.ext.UnderTowConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//Servlet模式
public class UnderServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServerExchange exchange = ((HttpServletRequestImpl) req).getExchange();
        UtHttpServletContext context = new UtHttpServletContext(req, resp,exchange);
        context.contentType("text/plain;charset=UTF-8");
        try {
            UnderTowConfig.app.handle(context);

            if(context.getHandled() && context.status() != 404){
              return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            if( UnderTowConfig.debug) {
                ex.printStackTrace(resp.getWriter());
                resp.setStatus(500);
                return;
            }else{
                throw new ServletException(ex);
            }
        }

    }
}