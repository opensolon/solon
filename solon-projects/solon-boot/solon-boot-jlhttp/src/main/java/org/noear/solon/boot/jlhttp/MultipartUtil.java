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
package org.noear.solon.boot.jlhttp;

import org.noear.jlhttp.HTTPServer;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.http.HttpPartFile;
import org.noear.solon.boot.io.LimitedInputException;
import org.noear.solon.boot.io.LimitedInputStream;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.MultiMap;

import java.io.IOException;

class MultipartUtil {
    public static void buildParamsAndFiles(JlHttpContext ctx, MultiMap<UploadedFile> filesMap) {
        try {
            HTTPServer.Request request = (HTTPServer.Request) ctx.request();
            HTTPServer.MultipartIterator parts = new HTTPServer.MultipartIterator(request);

            while (parts.hasNext()) {
                HTTPServer.MultipartIterator.Part part = parts.next();
                String name = ServerProps.urlDecode(part.getName());

                if (isFile(part)) {
                    doBuildFiles(name, filesMap, part);
                } else {
                    ctx.paramMap().add(name, part.getString());
                }
            }
        } catch (Exception e) {
            throw status4xx(ctx, e);
        }
    }

    public static StatusException status4xx(Context ctx, Exception e) {
        if (e instanceof StatusException) {
            return (StatusException) e;
        } else {
            if (isBodyLargerEx(e)) {
                return new StatusException("Request Entity Too Large: " + ctx.method() + " " + ctx.pathNew(), e, 413);
            } else {
                return new StatusException("Bad Request:" + ctx.method() + " " + ctx.pathNew(), e, 400);
            }
        }
    }

    private static void doBuildFiles(String name, MultiMap<UploadedFile> filesMap, HTTPServer.MultipartIterator.Part part) throws IOException {
        KeyValues<UploadedFile> list = filesMap.holder(name);

        String contentType = part.getHeaders().get("Content-Type");
        String filename = part.getFilename();
        String extension = null;
        int idx = filename.lastIndexOf(".");
        if (idx > 0) {
            extension = filename.substring(idx + 1);
        }

        HttpPartFile partFile = new HttpPartFile(filename, new LimitedInputStream(part.getBody(), ServerProps.request_maxFileSize));
        UploadedFile f1 = new UploadedFile(partFile::delete, contentType, partFile.getSize(), partFile.getContent(), filename, extension);

        list.addValue(f1);
    }

    private static boolean isField(HTTPServer.MultipartIterator.Part filePart) {
        return filePart.getFilename() == null;
    }

    private static boolean isFile(HTTPServer.MultipartIterator.Part filePart) {
        return !isField(filePart);
    }

    /**
     * 是否为 body larger ex?
     */
    public static boolean isBodyLargerEx(Throwable e) {
        return hasLargerStr(e) || hasLargerStr(e.getCause());
    }

    private static boolean hasLargerStr(Throwable e) {
        if (e instanceof LimitedInputException) {
            return true;
        }

        if (e == null || e.getMessage() == null) {
            return false;
        } else {
            return e.getMessage().contains("too large");
        }
    }
}