package org.noear.solon.boot.jlhttp;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.io.LimitedInputStream;
import org.noear.solon.core.handle.UploadedFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class MultipartUtil {
    public static void buildParamsAndFiles(JlHttpContext context) throws IOException {
        HTTPServer.Request request = (HTTPServer.Request) context.request();
        HTTPServer.MultipartIterator parts = new HTTPServer.MultipartIterator(request);

        while (parts.hasNext()) {
            HTTPServer.MultipartIterator.Part part = parts.next();

            if (isFile(part) == false) {
                context.paramSet(part.name, part.getString());
            } else {
                doBuildFiles(context, part);
            }
        }
    }

    private static void doBuildFiles(JlHttpContext context, HTTPServer.MultipartIterator.Part part) throws IOException {
        List<UploadedFile> list = context._fileMap.get(part.getName());
        if (list == null) {
            list = new ArrayList<>();
            context._fileMap.put(part.getName(), list);
        }


        String contentType = part.getHeaders().get("Content-Type");
        InputStream content = read(new LimitedInputStream(part.getBody(), ServerProps.request_maxFileSize));
        int contentSize = content.available();
        String name = part.getFilename();
        String extension = null;
        int idx = name.lastIndexOf(".");
        if (idx > 0) {
            extension = name.substring(idx + 1);
        }

        UploadedFile f1 = new UploadedFile(contentType, contentSize, content, name, extension);

        list.add(f1);
    }

    private static boolean isField(HTTPServer.MultipartIterator.Part filePart) {
        return filePart.getFilename() == null;
    }

    private static boolean isFile(HTTPServer.MultipartIterator.Part filePart) {
        return !isField(filePart);
    }

    private static ByteArrayInputStream read(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return new ByteArrayInputStream(output.toByteArray());
    }
}
