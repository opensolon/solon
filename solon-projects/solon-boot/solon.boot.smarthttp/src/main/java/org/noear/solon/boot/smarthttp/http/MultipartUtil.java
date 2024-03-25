package org.noear.solon.boot.smarthttp.http;


import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.http.HttpPartFile;
import org.noear.solon.boot.smarthttp.http.uploadfile.HttpMultipart;
import org.noear.solon.boot.smarthttp.http.uploadfile.HttpMultipartCollection;
import org.noear.solon.boot.io.LimitedInputStream;
import org.noear.solon.core.handle.UploadedFile;
import org.smartboot.http.server.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class MultipartUtil {
    public static void buildParamsAndFiles(SmHttpContext context, Map<String, List<UploadedFile>> filesMap) throws IOException {
        HttpRequest request = (HttpRequest) context.request();
        HttpMultipartCollection parts = new HttpMultipartCollection(request);

        while (parts.hasNext()) {
            HttpMultipart part = parts.next();

            if (isFile(part) == false) {
                context.paramSet(part.name, part.getString());
            } else {
                doBuildFiles(context, filesMap, part);
            }
        }
    }

    private static void doBuildFiles(SmHttpContext context, Map<String, List<UploadedFile>> filesMap, HttpMultipart part) throws IOException {
        List<UploadedFile> list = filesMap.get(part.getName());
        if (list == null) {
            list = new ArrayList<>();
            filesMap.put(part.getName(), list);
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