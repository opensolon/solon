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
package org.noear.solon.boot.jetty;

import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.noear.solon.boot.jetty.http.JtJspStarter;
import org.noear.solon.boot.jetty.jsp.JspTldLocator;

import javax.servlet.ServletContext;
import javax.servlet.descriptor.TaglibDescriptor;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

public class JettyServerAddJsp extends JettyServer {

    /**
     * 获取Server Handler
     */
    @Override
    protected Handler buildHandler() throws IOException {
        ServletContextHandler handler = getServletHandler();

        enableJspSupport(handler);
        addTdlSupport(handler.getServletContext());

        return handler;
    }

    private void enableJspSupport(ServletContextHandler handler) throws IOException {

        // Set Classloader of Context to be sane (needed for JSTL)
        // JSP requires a non-System classloader, this simply wraps the
        // embedded System classloader in a way that makes it suitable
        // for JSP to use
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
        handler.setClassLoader(jspClassLoader);

        // Manually call JettyJasperInitializer on context startup
        handler.addBean(new JtJspStarter(handler));

        // Create / Register JSP Servlet (must be named "jsp" per spec)
        ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
        holderJsp.setInitOrder(0);

        handler.addServlet(holderJsp, "*.jsp");
    }

    private void addTdlSupport(ServletContext servletContext) throws IOException {
        Map<String, TaglibDescriptor> tagLibInfos = JspTldLocator.createTldInfos("templates");

        if (tagLibInfos.size() > 0) {
            ServletContextHandler.JspConfig jspConfig = (ServletContextHandler.JspConfig) servletContext.getJspConfigDescriptor();
            if (jspConfig == null) {
                jspConfig = new ServletContextHandler.JspConfig();
                ((ServletContextHandler.Context) servletContext).setJspConfigDescriptor(jspConfig);
            }

            for (TaglibDescriptor descriptor : tagLibInfos.values()) {
                jspConfig.addTaglibDescriptor(descriptor);
            }
        }
    }
}
