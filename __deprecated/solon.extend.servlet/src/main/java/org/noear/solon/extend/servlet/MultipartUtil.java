package org.noear.solon.extend.servlet;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.handle.UploadedFile;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.*;

/**
 * @author noear
 * @since 1.2
 * */
@Deprecated
class MultipartUtil {

    public static void buildParamsAndFiles(SolonServletContext context) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) context.request();

        long maxBodySize = (ServerProps.request_maxBodySize == 0 ? -1L : ServerProps.request_maxBodySize);
        long maxFileSize = (ServerProps.request_maxFileSize == 0 ? -1L : ServerProps.request_maxFileSize);

        MultipartConfigElement configElement = new MultipartConfigElement(
                System.getProperty("java.io.tmpdir"),
                maxFileSize,
                maxBodySize,
                0);

        request.setAttribute("org.eclipse.jetty.multipartConfig", configElement);

        for (Part part : request.getParts()) {
            if (isFile(part)) {
                doBuildFiles(context, part);
            }
        }
    }

    private static void doBuildFiles(SolonServletContext context, Part part) throws IOException {
        List<UploadedFile> list = context._fileMap.get(part.getName());
        if (list == null) {
            list = new ArrayList<>();
            context._fileMap.put(part.getName(), list);
        }

        UploadedFile f1 = new UploadedFile();
        f1.contentType = part.getContentType();
        f1.contentSize = part.getSize();
        f1.content = part.getInputStream(); //可以转成 ByteArrayInputStream

        f1.name = part.getSubmittedFileName();
        int idx = f1.name.lastIndexOf(".");
        if (idx > 0) {
            f1.extension = f1.name.substring(idx + 1);
        }

        list.add(f1);
    }

    private static boolean isField(Part filePart) {
        return filePart.getSubmittedFileName() == null;
    }

    private static boolean isFile(Part filePart) {
        return !isField(filePart);
    }
}
