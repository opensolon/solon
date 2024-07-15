/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.boot.jdkhttp;


import com.sun.net.httpserver.HttpExchange;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.http.HttpPartFile;
import org.noear.solon.boot.jdkhttp.uploadfile.HttpMultipart;
import org.noear.solon.boot.jdkhttp.uploadfile.HttpMultipartCollection;
import org.noear.solon.boot.io.LimitedInputStream;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.UploadedFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class MultipartUtil {
    public static void buildParamsAndFiles(JdkHttpContext ctx, Map<String, List<UploadedFile>> filesMap) throws IOException {
        try {
            HttpMultipartCollection parts = new HttpMultipartCollection((HttpExchange) ctx.request());

            while (parts.hasNext()) {
                HttpMultipart part = parts.next();
                String name = ServerProps.urlDecode(part.getName());

                if (isFile(part)) {
                    doBuildFiles(name, filesMap, part);
                } else {
                    ctx.paramSet(name, part.getString());
                }
            }
        } catch (Exception e) {
            throw new StatusException("Bad Request: " + ctx.method() + " " + ctx.pathNew(), e, 400);
        }
    }

    private static void doBuildFiles(String name, Map<String, List<UploadedFile>> filesMap, HttpMultipart part) throws IOException {
        List<UploadedFile> list = filesMap.get(name);
        if (list == null) {
            list = new ArrayList<>();
            filesMap.put(name, list);
        }

        String contentType = part.getHeaders().get("Content-Type");
        String filename = part.getFilename();
        String extension = null;
        int idx = filename.lastIndexOf(".");
        if (idx > 0) {
            extension = filename.substring(idx + 1);
        }
        HttpPartFile partFile = new HttpPartFile(filename, new LimitedInputStream(part.getBody(), ServerProps.request_maxFileSize));

        UploadedFile f1 = new UploadedFile(partFile::delete, contentType, partFile.getSize(), partFile.getContent(), filename, extension);

        list.add(f1);
    }

    private static boolean isField(HttpMultipart filePart) {
        return filePart.getFilename() == null;
    }

    private static boolean isFile(HttpMultipart filePart) {
        return !isField(filePart);
    }
}