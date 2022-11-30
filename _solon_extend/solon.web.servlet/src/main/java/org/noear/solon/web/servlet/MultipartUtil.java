package org.noear.solon.web.servlet;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.handle.UploadedFile;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 1.2
 * */
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

        String contentType = part.getContentType();
        long contentSize = part.getSize();
        InputStream content = part.getInputStream(); //可以转成 ByteArrayInputStream
        String name = part.getSubmittedFileName();
        String extension = null;
        int idx = name.lastIndexOf(".");
        if (idx > 0) {
            extension = name.substring(idx + 1);
        }

        UploadedFile f1 = new UploadedFile(contentType, contentSize, content, name, extension);

        list.add(f1);
    }

    private static boolean isField(Part filePart) {
        return filePart.getSubmittedFileName() == null;
    }

    private static boolean isFile(Part filePart) {
        return !isField(filePart);
    }
}
