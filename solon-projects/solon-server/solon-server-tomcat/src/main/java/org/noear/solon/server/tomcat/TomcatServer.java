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
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.handle.SessionProps;
import org.noear.solon.server.tomcat.http.TCHttpContextHandler;

import javax.servlet.MultipartConfigElement;

/**
 * @author Yukai
 * @since 2019/3/28 15:49
 */
public class TomcatServer extends TomcatServerBase {
    @Override
    protected Connector addConnector(int port) throws Throwable {
        Connector connector = _server.getConnector();

        connector.setPort(port);
        connector.setMaxPostSize(ServerProps.request_maxBodySizeAsInt());
        connector.setProperty("maxHttpHeaderSize", String.valueOf(ServerProps.request_maxHeaderSize));
        connector.setProperty("relaxedQueryChars", "[]|{}");
        connector.setURIEncoding(ServerProps.request_encoding);
        connector.setUseBodyEncodingForURI(true);

        return connector;
    }

    @Override
    protected Context initContext() {
        Context context = _server.addContext("/", null);//第二个参数与文档相关

        String _tempdir = IoUtil.getTempDirAsString("solon-server");

        MultipartConfigElement multipartConfig = new MultipartConfigElement(
                _tempdir,
                ServerProps.request_maxFileSize,
                ServerProps.request_maxFileRequestSize(),
                ServerProps.request_fileSizeThreshold);

        context.getServletContext().setAttribute("org.apache.catalina.MultipartConfigElement", multipartConfig);
        context.setAllowCasualMultipartParsing(true);

        if (SessionProps.session_timeout > 0) {
            context.setSessionTimeout(SessionProps.session_timeout);
        }

        // for http
        Tomcat.addServlet(context, "solon", new TCHttpContextHandler())
                .setAsyncSupported(true);

        context.addServletMappingDecoded("/", "solon");//Servlet与对应uri映射

        //servlet 临时目录（用于生成 jsp 之类）
        _server.setBaseDir(_tempdir);

        return context;
    }
}