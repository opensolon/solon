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
package org.noear.solon.web.webservices.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Set;

/**
 * @author noear
 * @since 1.0
 */
public class WebServiceContainerInitializerImpl implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        //@WebServlet(name = "ws", urlPatterns = "/ws/*", loadOnStartup = 0)
        String path = Solon.cfg().get("server.webservices.path");

        if (Utils.isEmpty(path)) {
            path = "/ws/*";
        } else {
            if (path.startsWith("/") == false) {
                path = "/" + path;
            }

            if (path.endsWith("/")) {
                path = path + "*";
            } else {
                path = path + "/*";
            }
        }

        ServletRegistration.Dynamic registration = servletContext.addServlet("WebServiceServlet", WebServiceServlet.class);
        if (registration != null) {
            registration.setLoadOnStartup(0);
            registration.addMapping(path);
        }
    }
}
