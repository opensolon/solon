package org.noear.solon.boot.smarthttp.http;


import org.noear.solon.boot.ServerProps;
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

class MultipartUtil {
    public static void buildParamsAndFiles(SmartHttpContext context) throws IOException{
        HttpRequest request = (HttpRequest) context.request();
        HttpMultipartCollection parts = new HttpMultipartCollection(request);

        while (parts.hasNext()){
            HttpMultipart part = parts.next();

            if(isFile(part) == false){
                context.paramSet(part.name, part.getString());
            }else{
                doBuildFiles(context, part);
            }
        }
    }

    private static void doBuildFiles(SmartHttpContext context, HttpMultipart part) throws IOException{
        List<UploadedFile> list = context._fileMap.get(part.getName());
        if(list == null){
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

    private static boolean isField(HttpMultipart filePart){
        return filePart.getFilename() == null;
    }

    private static boolean isFile(HttpMultipart filePart){
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
