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
package org.noear.solon.web.servlet;

import org.noear.solon.server.ServerProps;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.MultiMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author noear
 * @since 1.2
 * @since 2.9
 * */
class MultipartUtil {
    public static void buildParamsAndFiles(SolonServletContext ctx, MultiMap<UploadedFile> filesMap) {
        try {
            HttpServletRequest request = (HttpServletRequest) ctx.request();

            for (Part part : request.getParts()) {
                String name = ServerProps.urlDecode(part.getName());

                if (isFile(part)) {
                    doBuildFiles(name, filesMap, part);
                } else {
                    ctx.paramMap().add(name, IoUtil.transferToString(part.getInputStream(), ServerProps.request_encoding));
                }
            }
        } catch (Exception e) {
            throw status4xx(ctx, e);
        }
    }

    public static StatusException status4xx(Context ctx, Exception e) {
        if (e instanceof StatusException) {
            return  (StatusException) e;
        } else {
            if (isBodyLargerEx(e)) {
                return new StatusException("Request Entity Too Large: " + ctx.method() + " " + ctx.pathNew(), e, 413);
            } else {
                return new StatusException("Bad Request:" + ctx.method() + " " + ctx.pathNew(), e, 400);
            }
        }
    }

    private static void doBuildFiles(String name, MultiMap<UploadedFile> filesMap, Part part) throws IOException {
        KeyValues<UploadedFile> list = filesMap.holder(name);

        String contentType = part.getContentType();
        long contentSize = part.getSize();
        InputStream content = part.getInputStream(); //可以转成 ByteArrayInputStream
        String fileName = part.getSubmittedFileName();
        String extension = null;
        int idx = fileName.lastIndexOf(".");
        if (idx > 0) {
            extension = fileName.substring(idx + 1);
        }

        UploadedFile f1 = new UploadedFile(part::delete, contentType, contentSize, content, fileName, extension);

        list.addValue(f1);
    }

    private static boolean isField(Part filePart) {
        return filePart.getSubmittedFileName() == null;
    }

    private static boolean isFile(Part filePart) {
        return !isField(filePart);
    }

    /**
     * 是否为 body larger ex?
     */
    public static boolean isBodyLargerEx(Throwable e) {
        return hasLargerStr(e) || hasLargerStr(e.getCause());
    }

    private static boolean hasLargerStr(Throwable e) {
        if (e == null || e.getMessage() == null) {
            return false;
        } else {
            return e.getMessage().contains("larger than");
        }
    }
}