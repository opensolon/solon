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
package org.noear.solon.boot.jetty.http;

import org.eclipse.jetty.http.MultiPartFormInputStream;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.jetty.integration.JettyPlugin;
import org.noear.solon.web.servlet.SolonServletHandler;
import org.noear.solon.core.handle.Context;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class JtHttpContextServletHandler extends SolonServletHandler {
    private File _tempdir;

    private int _fileOutputBuffer = 0;

    private long _maxBodySize;
    private long _maxFileSize;

    @Override
    public void init() throws ServletException {
        super.init();

        _tempdir = (File) getServletContext().getAttribute("javax.servlet.context.tempdir");

        String fileOutputBuffer = getServletConfig().getInitParameter("fileOutputBuffer");
        if (fileOutputBuffer != null) {
            _fileOutputBuffer = Integer.parseInt(fileOutputBuffer);
        }

        _maxBodySize = (ServerProps.request_maxBodySize > 0 ? ServerProps.request_maxBodySize : -1L);
        _maxFileSize = (ServerProps.request_maxFileSize > 0 ? ServerProps.request_maxFileSize : -1L);
    }

    @Override
    protected void preHandle(Context ctx) throws IOException {
        if (ServerProps.output_meta) {
            ctx.headerSet("Solon-Boot", JettyPlugin.solon_boot_ver());
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) {
            //如果是文件上传
            //
            MultipartConfigElement config = new MultipartConfigElement(
                    _tempdir.getCanonicalPath(),
                    _maxFileSize,
                    _maxBodySize,
                    _fileOutputBuffer);

            request.setAttribute("org.eclipse.jetty.multipartConfig", config);

            if (ServerProps.request_useTempfile) {
                //如果使用临时文件
                //
                InputStream in = new BufferedInputStream(request.getInputStream());
                String ct = request.getContentType();


                MultiPartFormInputStream multiPartParser = new MultiPartFormInputStream(in, ct, config, _tempdir);
                multiPartParser.setWriteFilesWithFilenames(true);

                request = new JtHttpRequestWrapper(request, multiPartParser);
            }
        }

        super.service(request, response);
    }
}
