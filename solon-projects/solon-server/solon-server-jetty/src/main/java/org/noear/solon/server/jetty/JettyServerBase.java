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

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.noear.solon.Utils;
import org.noear.solon.server.ServerConstants;
import org.noear.solon.server.ServerLifecycle;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.jetty.http.JtContainerInitializer;
import org.noear.solon.server.jetty.http.JtHttpContextServletHandler;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.server.http.HttpServerConfigure;
import org.noear.solon.server.ssl.SslConfig;
import org.noear.solon.server.handle.SessionProps;
import org.noear.solon.core.util.ResourceUtil;

import javax.net.ssl.SSLContext;
import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;

abstract class JettyServerBase implements ServerLifecycle , HttpServerConfigure {
    protected final HttpServerProps props;
    protected Executor executor;
    protected SslConfig sslConfig = new SslConfig(ServerConstants.SIGNAL_HTTP);
    protected boolean enableSessionState;
    protected boolean isSecure;

    public JettyServerBase(HttpServerProps props) {
        this.props = props;
    }


    public boolean isSecure() {
        return isSecure;
    }

    public void enableSessionState(boolean enableSessionState) {
        this.enableSessionState = enableSessionState;
    }


    protected Set<Integer> addHttpPorts = new LinkedHashSet<>();


    /**
     * 是否允许Ssl
     */
    @Override
    public void enableSsl(boolean enable, SSLContext sslContext) {
        sslConfig.set(enable, null);
    }

    /**
     * 添加 HttpPort（当 ssl 时，可再开个 http 端口）
     */
    @Override
    public void addHttpPort(int port) {
        addHttpPorts.add(port);
    }

    public HttpServerProps getProps() {
        return props;
    }

    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * 创建连接器（支持https）
     *
     * @since 1.6
     */
    protected ServerConnector getConnector(Server server, String host, int port, boolean autoSsl) throws RuntimeException {
        //配置 //http://www.eclipse.org/jetty/documentation/jetty-9/index.html
        HttpConfiguration config = new HttpConfiguration();
        if (ServerProps.request_maxHeaderSize > 0) {
            config.setRequestHeaderSize(ServerProps.request_maxHeaderSize);
        }
        config.setSendServerVersion(false);

        HttpConnectionFactory httpFactory = new HttpConnectionFactory(config);
        ServerConnector serverConnector;

        if (sslConfig.isSslEnable() && autoSsl) {

            String sslKeyStore = sslConfig.getProps().getSslKeyStore();
            String sslKeyStoreType = sslConfig.getProps().getSslKeyType();
            String sslKeyStorePassword = sslConfig.getProps().getSslKeyPassword();

            SslContextFactory.Server contextFactory = new SslContextFactory.Server();

            if (Utils.isNotEmpty(sslKeyStore)) {
                URL url = ResourceUtil.findResource(sslKeyStore);
                if (url != null) {
                    sslKeyStore = url.toString();
                }

                contextFactory.setKeyStorePath(sslKeyStore);
            }

            if (Utils.isNotEmpty(sslKeyStoreType)) {
                contextFactory.setKeyStoreType(sslKeyStoreType);
            }

            if (Utils.isNotEmpty(sslKeyStorePassword)) {
                contextFactory.setKeyStorePassword(sslKeyStorePassword);
            }

            SslConnectionFactory sslFactory = new SslConnectionFactory(contextFactory, HttpVersion.HTTP_1_1.asString());

            serverConnector = new ServerConnector(server, executor, null, null, -1, -1, sslFactory, httpFactory);
            //this(server, (Executor)null, (Scheduler)null, (ByteBufferPool)null, -1, -1, factories);
            isSecure = true;
        } else {
            serverConnector = new ServerConnector(server, executor, null, null, -1, -1, httpFactory);
            //this(server, (Executor)null, (Scheduler)null, (ByteBufferPool)null, -1, -1, factories);
        }

        serverConnector.setIdleTimeout(props.getIdleTimeoutOrDefault());
        serverConnector.setPort(port);

        if (Utils.isNotEmpty(host)) {
            serverConnector.setHost(host);
        }

        return serverConnector;
    }

    protected ServletContextHandler getServletHandler() throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File scratchDir = new File(tempDir, "solon-server");
        if(scratchDir.exists() == false){
            scratchDir.mkdirs();
        }

        String _tempdir = scratchDir.getAbsolutePath();
        int _fileOutputBuffer = 0;
        long _maxBodySize = (ServerProps.request_maxBodySize > 0 ? ServerProps.request_maxBodySize : -1L);
        long _maxFileSize = (ServerProps.request_maxFileSize > 0 ? ServerProps.request_maxFileSize : -1L);

        if (_maxBodySize < _maxFileSize) {
            _maxBodySize = _maxFileSize; //maxRequestSize
        }

        MultipartConfigElement multipartConfig = new MultipartConfigElement(
                _tempdir,
                _maxFileSize,
                _maxBodySize,
                _fileOutputBuffer);

        ServletHolder servletHolder = new ServletHolder(new JtHttpContextServletHandler());
        servletHolder.setAsyncSupported(true);
        servletHolder.getRegistration().setMultipartConfig(multipartConfig);

        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.addServlet(servletHolder, "/");


        //添加session state 支持
        if (enableSessionState) {
            handler.setSessionHandler(new SessionHandler());

            if (SessionProps.session_timeout > 0) {
                handler.getSessionHandler().setMaxInactiveInterval(SessionProps.session_timeout);
            }
        }

        //添加容器初始器
        handler.addLifeCycleListener(new JtContainerInitializer(handler.getServletContext()));

        //添加临时文件（用于jsp编译，或文件上传）
        if (!scratchDir.exists()) {
            if (!scratchDir.mkdirs()) {
                throw new IOException("Unable to create scratch directory: " + scratchDir);
            }
        }
        handler.setAttribute("javax.servlet.context.tempdir", scratchDir);

        return handler;
    }
}
