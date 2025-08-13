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
package org.noear.solon.server.reactornetty;

import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.noear.solon.core.handle.UploadedFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对 Request 的参数进行解析
 * */
class HttpRequestParser {
    private final DefaultFullHttpRequest _request;

    protected final Map<String, List<String>> parmMap = new HashMap<>();
    protected final Map<String, List<UploadedFile>> fileMap = new HashMap<>();

    public HttpRequestParser(DefaultFullHttpRequest req) {
        _request = req;
    }

    protected HttpRequestParser parse() throws Exception {
        HttpMethod method = _request.method();

        // url 参数
        if (_request.uri().indexOf("?") > 0) {
            QueryStringDecoder decoder = new QueryStringDecoder(_request.uri());

            decoder.parameters().entrySet().forEach(entry -> {
                parmMap.put(entry.getKey(), entry.getValue());
            });
        }


        // body
        if (HttpMethod.POST == method
                || HttpMethod.PUT == method
                || HttpMethod.DELETE == method
                || HttpMethod.PATCH == method) {

            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(_request);
            decoder.offer(_request);

            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();

            for (InterfaceHttpData p1 : parmList) {
                if (p1.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    //参数
                    //
                    Attribute a1 = (Attribute) p1;

                    List<String> tmp = parmMap.get(a1.getName());
                    if (tmp == null) {
                        tmp = new ArrayList<>();
                        parmMap.put(a1.getName(), tmp);
                    }

                    tmp.add(a1.getValue());
                    continue;
                }

                if (p1.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                    //文件
                    //
                    FileUpload f0 = (FileUpload) p1;

                    List<UploadedFile> tmp = fileMap.get(p1.getName());
                    if (tmp == null) {
                        tmp = new ArrayList<>();
                        fileMap.put(p1.getName(), tmp);
                    }


                    String contentType = f0.getContentType();
                    InputStream content = new FileInputStream(f0.getFile());
                    int contentSize = content.available();
                    String name = f0.getFilename();
                    String extension = null;
                    int idx = name.lastIndexOf(".");
                    if (idx > 0) {
                        extension = name.substring(idx + 1);
                    }

                    UploadedFile f1 = new UploadedFile(f0::delete, contentType, contentSize, content, name, extension);

                    tmp.add(f1);
                }
            }
        }

        return this;
    }
}
