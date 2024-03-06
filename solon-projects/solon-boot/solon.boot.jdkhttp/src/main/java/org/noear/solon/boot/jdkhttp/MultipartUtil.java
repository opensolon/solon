package org.noear.solon.boot.jdkhttp;


import com.sun.net.httpserver.HttpExchange;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.http.HttpPartFile;
import org.noear.solon.boot.jdkhttp.uploadfile.HttpMultipart;
import org.noear.solon.boot.jdkhttp.uploadfile.HttpMultipartCollection;
import org.noear.solon.boot.io.LimitedInputStream;
import org.noear.solon.core.handle.UploadedFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class MultipartUtil {
    public static void buildParamsAndFiles(JdkHttpContext context) throws IOException {
        HttpMultipartCollection parts = new HttpMultipartCollection((HttpExchange) context.request());

        while (parts.hasNext()) {
            HttpMultipart part = parts.next();
            if (isFile(part) == false) {
                context.paramSet(part.name, part.getString());
            } else {
                doBuildFiles(context, part);
            }
        }
    }

    private static void doBuildFiles(JdkHttpContext context, HttpMultipart part) throws IOException {
        List<UploadedFile> list = context._fileMap.get(part.getName());
        if (list == null) {
            list = new ArrayList<>();
            context._fileMap.put(part.getName(), list);
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
