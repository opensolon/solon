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
package org.noear.solon.boot.nettyhttp;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.io.LimitedInputStream;
import org.noear.solon.boot.nettyhttp.uploadfile.HttpMultipart;
import org.noear.solon.boot.nettyhttp.uploadfile.HttpMultipartCollection;
import org.noear.solon.core.handle.UploadedFile;

class MultipartUtil {

    public static void buildParamsAndFiles(final NettyHttpContext context) throws IOException {
        HttpMultipartCollection parts = new HttpMultipartCollection(context);

        while (parts.hasNext()) {
            HttpMultipart part = parts.next();
            if (isFile(part) == false) {
                context.paramSet(part.name, part.getString());
            } else {
                doBuildFiles(context, part);
            }
        }
    }

    private static void doBuildFiles(final NettyHttpContext context, final HttpMultipart part)
            throws IOException {

        String contentType = part.getHeaders().get("Content-Type");
        InputStream content = read(
                new LimitedInputStream(part.getBody(), ServerProps.request_maxFileSize));
        int contentSize = content.available();
        String name = part.getFilename();
        String extension = null;
        int idx = name.lastIndexOf(".");
        if (idx > 0) {
            extension = name.substring(idx + 1);
        }

        context._fileMap
                .computeIfAbsent(part.getName(), k -> new ArrayList<>())
                .add(new UploadedFile(contentType, contentSize, content, name, extension));
    }

    private static boolean isField(final HttpMultipart filePart) {
        return filePart.getFilename() == null;
    }

    private static boolean isFile(final HttpMultipart filePart) {
        return !isField(filePart);
    }

    private static ByteArrayInputStream read(final InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return new ByteArrayInputStream(output.toByteArray());
    }
}
