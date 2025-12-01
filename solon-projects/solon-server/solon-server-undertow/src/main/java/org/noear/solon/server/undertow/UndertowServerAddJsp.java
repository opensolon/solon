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
package org.noear.solon.server.undertow;

import io.undertow.jsp.HackInstanceManager;
import io.undertow.jsp.JspServletBuilder;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.*;
import org.apache.jasper.deploy.JspPropertyGroup;
import org.apache.jasper.deploy.TagLibraryInfo;
import org.noear.solon.Solon;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.server.undertow.http.UtHttpContextServletHandler;
import org.noear.solon.server.undertow.jsp.JspResourceManager;
import org.noear.solon.server.undertow.jsp.JspServletEx;
import org.noear.solon.server.undertow.jsp.JspTldLocator;
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.server.util.DebugUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author by: Yukai
 * @since: 2019/3/28 15:50
 */
public class UndertowServerAddJsp extends UndertowServer {

    public UndertowServerAddJsp(HttpServerProps props) {
        super(props);
    }

    @Override
    protected HttpHandler buildHandler() throws Exception {
        DeploymentInfo builder = initDeploymentInfo();

        //添加jsp处理
        String resRoot = getResourceRoot();
        builder.setResourceManager(new JspResourceManager(AppClassLoader.global(), resRoot));

        builder.addServlet(new ServletInfo("ACTServlet", UtHttpContextServletHandler.class)
                        .addMapping("/")
                        .setAsyncSupported(true))
                .addServlet(JspServletEx.createServlet("JSPServlet", "*.jsp"));


        //添加taglib支持
        Map<String, TagLibraryInfo> tagLibraryMap = JspTldLocator.createTldInfos("WEB-INF", "templates");
        JspServletBuilder.setupDeployment(builder, new HashMap<String, JspPropertyGroup>(), tagLibraryMap, new HackInstanceManager());


        //开始部署
        final ServletContainer container = ServletContainer.Factory.newInstance();
        DeploymentManager manager = container.addDeployment(builder);
        manager.deploy();

        return manager.start();
    }

    protected String getResourceRoot() throws FileNotFoundException {
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
                dir.toURI().getPath();
            }
        }

        return rootURL.getPath();
    }

    protected URL getRootPath() {
        URL root = ResourceUtil.getResource("/");
        if (root != null) {
            return root;
        }
        try {
            URL temp = ResourceUtil.getResource("");
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
