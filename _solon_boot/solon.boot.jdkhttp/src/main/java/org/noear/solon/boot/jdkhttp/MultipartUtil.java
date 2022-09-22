package org.noear.solon.boot.jdkhttp;


import com.sun.net.httpserver.HttpExchange;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.jdkhttp.uploadfile.HttpMultipart;
import org.noear.solon.boot.jdkhttp.uploadfile.HttpMultipartCollection;
import org.noear.solon.boot.io.LimitedInputStream;
import org.noear.solon.core.handle.UploadedFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class MultipartUtil {
    public static void buildParamsAndFiles(JdkHttpContext context) throws IOException{
        HttpMultipartCollection parts = new HttpMultipartCollection((HttpExchange) context.request());

        while (parts.hasNext()){
            HttpMultipart part = parts.next();
            if(isFile(part) == false){
                context.paramSet(part.name, part.getString());
            }else{
                doBuildFiles(context, part);
            }
        }
    }

    private static void doBuildFiles(JdkHttpContext context, HttpMultipart part) throws IOException{
        List<UploadedFile> list = context._fileMap.get(part.getName());
        if(list == null){
            list = new ArrayList<>();
            context._fileMap.put(part.getName(), list);
        }

        UploadedFile f1 = new UploadedFile();
        f1.contentType = part.getHeaders().get("Content-Type");
        f1.content = read(new LimitedInputStream(part.getBody(), ServerProps.request_maxFileSize));
        f1.contentSize = f1.content.available();
        f1.name = part.getFilename();
        int idx = f1.name.lastIndexOf(".");
        if (idx > 0) {
            f1.extension = f1.name.substring(idx + 1);
        }

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
