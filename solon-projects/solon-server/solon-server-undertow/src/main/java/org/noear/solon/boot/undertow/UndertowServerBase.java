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
package org.noear.solon.boot.undertow;

import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.util.DefaultClassIntrospector;
import org.noear.solon.Solon;
import org.noear.solon.server.ServerConstants;
import org.noear.solon.server.ServerLifecycle;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.server.ssl.SslConfig;
import org.noear.solon.boot.undertow.http.UtContainerInitializer;
import org.noear.solon.server.http.HttpServerConfigure;
import org.noear.solon.boot.undertow.integration.UndertowPlugin;
import org.noear.solon.server.handle.SessionProps;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.servlet.MultipartConfigElement;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;

abstract class UndertowServerBase implements ServerLifecycle, HttpServerConfigure {
    static final Logger log = LoggerFactory.getLogger(UndertowServerBase.class);

    protected final HttpServerProps props;
    protected SslConfig sslConfig = new SslConfig(ServerConstants.SIGNAL_HTTP);

    protected Set<Integer> addHttpPorts = new LinkedHashSet<>();

    protected boolean enableHttp2 = false;

    public UndertowServerBase(HttpServerProps props) {
        this.props = props;
    }

    /**
     * 是否允许Ssl
     */
    @Override
    public void enableSsl(boolean enable, @Nullable SSLContext sslContext) {
        sslConfig.set(enable, sslContext);
    }

    @Override
    public boolean isSupportedHttp2() {
        return true;
    }

    @Override
    public void enableHttp2(boolean enable) {
        this.enableHttp2 = enable;
    }

    public boolean isEnableHttp2(){
        return enableHttp2;
    }

    /**
     * 添加 HttpPort（当 ssl 时，可再开个 http 端口）
     */
    @Override
    public void addHttpPort(int port) {
        addHttpPorts.add(port);
    }

    @Override
    public void setExecutor(Executor executor) {
        log.warn("Undertow does not support user-defined executor");
    }

    public HttpServerProps getProps() {
        return props;
    }

    protected DeploymentInfo initDeploymentInfo() {
        MultipartConfigElement configElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));

        DeploymentInfo builder = new DeploymentInfo()
                .setClassLoader(UndertowPlugin.class.getClassLoader())
                .setDeploymentName("solon")
                .setContextPath("/")
                .setDefaultEncoding(ServerProps.request_encoding)
                .setDefaultMultipartConfig(configElement)
                .setClassIntrospecter(DefaultClassIntrospector.INSTANCE);

        //添加容器初始器
        builder.addServletContainerInitializer(UtContainerInitializer.info());
        builder.setEagerFilterInit(true);

        if (SessionProps.session_timeout > 0) {
            builder.setDefaultSessionTimeout(SessionProps.session_timeout);
        }

        return builder;
    }

    protected String getResourceRoot() throws FileNotFoundException {
        URL rootURL = getRootPath();
        if (rootURL == null) {
            if (NativeDetector.inNativeImage()) {
                return "";
            }

            throw new FileNotFoundException("Unable to find root");
        }

        String resURL = rootURL.toString();

        if (Solon.cfg().isDebugMode() && (resURL.startsWith("jar:") == false)) {
            int endIndex = resURL.indexOf("target");
            return resURL.substring(0, endIndex) + "src/main/resources/";
        }

        return "";
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