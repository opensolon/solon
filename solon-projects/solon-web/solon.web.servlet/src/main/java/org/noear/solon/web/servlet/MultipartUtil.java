package org.noear.solon.web.servlet;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.IoUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author noear
 * @since 1.2
 * */
class MultipartUtil {
    public static void buildParamsAndFiles(SolonServletContext context, Map<String, List<UploadedFile>> filesMap) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) context.request();

            for (Part part : request.getParts()) {
                String name = ServerProps.urlDecode(part.getName());

                if (isFile(part)) {
                    doBuildFiles(name, filesMap, part);
                } else {
                    context.paramSet(name, IoUtil.transferToString(part.getInputStream(), ServerProps.request_encoding));
                }
            }
        } catch (Exception e) {
            throw new StatusException("Bad Request", e, 400);
        }
    }

    private static void doBuildFiles(String name, Map<String, List<UploadedFile>> filesMap, Part part) throws IOException {
        List<UploadedFile> list = filesMap.get(name);
        if (list == null) {
            list = new ArrayList<>();
            filesMap.put(name, list);
        }

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

        list.add(f1);
    }

    private static boolean isField(Part filePart) {
        return filePart.getSubmittedFileName() == null;
    }

    private static boolean isFile(Part filePart) {
        return !isField(filePart);
    }
}