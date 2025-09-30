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
package org.noear.solon.server.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.coyote.http2.Http2Protocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.handle.SessionProps;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.server.tomcat.http.TCHttpContextHandler;
import org.noear.solon.server.tomcat.ssl.TomcatSslContext;

import javax.net.ssl.SSLContext;
import javax.servlet.MultipartConfigElement;
import java.io.IOException;

/**
 * @author Yukai
 * @author noear
 * @since 2019/3/28 15:49
 * @since 3.6
 */
public class TomcatServer extends TomcatServerBase {
    protected boolean isSecure;
    protected boolean enableWebSocket;

    public boolean isSecure() {
        return isSecure;
    }

    public void enableWebSocket(boolean enableWebSocket) {
        this.enableWebSocket = enableWebSocket;
    }

    public TomcatServer(HttpServerProps props) {
        super(props);
    }

    @Override
    protected Context initContext() {
        String _tempdir = IoUtil.getTempDirAsString("solon-server");

        // servlet 临时目录（用于生成 jsp 之类）
        _server.setBaseDir(_tempdir);

        // for context
        Context context = _server.addContext("/", null);//第二个参数与文档相关

        context.setAllowCasualMultipartParsing(true);

        if (SessionProps.session_timeout > 0) {
            context.setSessionTimeout(SessionProps.session_timeout);
        }

        // for http
        MultipartConfigElement multipartConfig = new MultipartConfigElement(
                _tempdir,
                ServerProps.request_maxFileSize,
                ServerProps.request_maxFileRequestSize(),
                ServerProps.request_fileSizeThreshold);

        Wrapper servlet = Tomcat.addServlet(context, "solon", new TCHttpContextHandler());
        servlet.setAsyncSupported(true);
        servlet.setMultipartConfigElement(multipartConfig);

        context.addServletMappingDecoded("/", "solon");//Servlet与对应uri映射

        return context;
    }

    @Override
    protected void addConnector(int port, boolean isMain) throws IOException {
        //::protocol
        ProtocolHandler protocol = createHttp11Protocol(isMain);

        if (isMain && enableHttp2) {
            protocol.addUpgradeProtocol(new Http2Protocol());
        }


        //::connector
        final Connector connector = new Connector(protocol);

        connector.setPort(port);

        if (isMain) {
            //for connector ssl
            if (sslConfig.isSslEnable()) {
                connector.setSecure(true);
                connector.setScheme("https");
            }
        }

        connector.setMaxPostSize(ServerProps.request_maxBodySizeAsInt());
        connector.setMaxPartHeaderSize(ServerProps.request_maxHeaderSize);
        connector.setURIEncoding(ServerProps.request_encoding);
        connector.setUseBodyEncodingForURI(true);

        _server.getService().addConnector(connector);
    }

    private ProtocolHandler createHttp11Protocol(boolean isMain) throws IOException {
        final Http11NioProtocol protocol = new Http11NioProtocol();

        if (ServerProps.request_maxHeaderSize > 0) {
            protocol.setMaxHttpHeaderSize(ServerProps.request_maxHeaderSize);
        }

        if (ServerProps.request_maxBodySize > 0) {
            protocol.setMaxSwallowSize(ServerProps.request_maxBodySizeAsInt());
        }

        protocol.setRelaxedQueryChars("[]|{}");

        if (isMain) {
            //for protocol ssl
            if (sslConfig.isSslEnable()) {
                protocol.setSSLEnabled(true);
                protocol.setSecure(true);
                protocol.addSslHostConfig(createSSLHostConfig(sslConfig.getSslContext()));
                isSecure = true;
            }
        }

        return protocol;
    }


    private static SSLHostConfig createSSLHostConfig(final SSLContext sslContext) {
        final SSLHostConfig sslHostConfig = new SSLHostConfig();

        final SSLHostConfigCertificate sslHostConfigCertificate =
                new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.RSA);
        sslHostConfigCertificate.setSslContext(new TomcatSslContext(sslContext));

        sslHostConfig.addCertificate(sslHostConfigCertificate);
        return sslHostConfig;
    }
}