package org.noear.solon.boot.jlhttp;


import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class MultipartUtil {
    public static List<XFile> getUploadedFiles(XContext context, String partName) throws IOException{
        HTTPServer.Request request = (HTTPServer.Request)context.request();

        HTTPServer.MultipartIterator parts = new HTTPServer.MultipartIterator(request);

        List<XFile> list = new ArrayList<>();

        while (parts.hasNext()){
            HTTPServer.MultipartIterator.Part part = parts.next();
            if(isFile(part) && part.getName().equals(partName)){
                XFile f = new XFile();
                f.contentType = part.getHeaders().get("Content-Type");
                f.content = read(part.getBody());
                f.name = part.getFilename();
                int idx = f.name.lastIndexOf(".");

                if (idx > 0) {
                    f.extension = f.name.substring(idx + 1);
                }

                list.add(f);
            }
        }

        return list;

    }

    private static boolean isField(HTTPServer.MultipartIterator.Part filePart){
        return filePart.getFilename() == null;
    }

    private static boolean isFile(HTTPServer.MultipartIterator.Part filePart){
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
