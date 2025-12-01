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
package org.noear.solon.server.jetty;

import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;
import org.noear.solon.Solon;
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.server.jetty.jsp.JspLifeCycle;
import org.noear.solon.server.jetty.jsp.JspTldLocator;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.server.util.DebugUtils;

import javax.servlet.ServletContext;
import javax.servlet.descriptor.TaglibDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.Map;

public class JettyServerAddJsp extends JettyServer {

    public JettyServerAddJsp(HttpServerProps props) {
        super(props);
    }

    /**
     * 获取Server Handler
     */
    @Override
    protected ServletContextHandler buildHandler() throws IOException, URISyntaxException {
        ServletContextHandler handler = getServletHandler();

        //jsp 资源的根目录
        String resRoot = getResourceRoot();
        handler.setResourceBase(resRoot);

        addJspSupport(handler);
        addTdlSupport(handler.getServletContext());

        return handler;
    }

    private void addJspSupport(ServletContextHandler handler) throws IOException {

        // Set Classloader of Context to be sane (needed for JSTL)
        // JSP requires a non-System classloader, this simply wraps the
        // embedded System classloader in a way that makes it suitable
        // for JSP to use
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
        handler.setClassLoader(jspClassLoader);

        // Manually call JettyJasperInitializer on context startup
        handler.addBean(new JspLifeCycle(handler));

        // Create / Register JSP Servlet (must be named "jsp" per spec)
        ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
        holderJsp.setInitOrder(0);

        handler.addServlet(holderJsp, "*.jsp");
    }

    private void addTdlSupport(ServletContext servletContext) throws IOException {
        Map<String, TaglibDescriptor> tagLibInfos = JspTldLocator.createTldInfos("WEB-INF", "templates");

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

    private String getResourceRoot() throws FileNotFoundException {
        URL rootURL = getRootPath();

        if (rootURL == null) {
            if (NativeDetector.inNativeImage()) {
                return null;
            }

            throw new FileNotFoundException("Unable to find root");
        }

        if (Solon.cfg().isDebugMode() && Solon.cfg().isFilesMode()) {
            File dir = DebugUtils.getDebugLocation(AppClassLoader.global(), "/");
            if (dir != null) {
                return dir.toURI().getPath();
            }
        }

        return rootURL.getPath();
    }

    private URL getRootPath() {
        URL root = ResourceUtil.getResource("/");
        if (root != null) {
            return root;
        }

        try {
            URL temp = ResourceUtil.getResource(""); //有些环境，/ 取不到根
            if (temp == null) {
                return null;
            }

            String path = temp.toString();
            if (path.startsWith("jar:")) {
                int endIndex = path.indexOf("!");
                path = path.substring(0, endIndex + 1) + "/";
            } else {
                return null;
            }
            return new URL(path);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}