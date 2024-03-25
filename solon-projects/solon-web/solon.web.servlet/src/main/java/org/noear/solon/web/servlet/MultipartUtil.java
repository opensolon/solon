package org.noear.solon.web.servlet;

import org.noear.solon.boot.ServerProps;
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
        HttpServletRequest request = (HttpServletRequest) context.request();

        for (Part part : request.getParts()) {
            if (isFile(part)) {
                doBuildFiles(context, filesMap, part);
            } else {
                if (request.getParameterMap() == null || request.getParameterMap().isEmpty()) {
                    context.paramSet(part.getName(), IoUtil.transferToString(part.getInputStream(), ServerProps.request_encoding));
                }
            }
        }
    }

    private static void doBuildFiles(SolonServletContext context, Map<String, List<UploadedFile>> filesMap, Part part) throws IOException {
        List<UploadedFile> list = filesMap.get(part.getName());
        if (list == null) {
            list = new ArrayList<>();
            filesMap.put(part.getName(), list);
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

        UploadedFile f1 = new UploadedFile(part::delete, contentType, contentSize, content, name, extension);

        list.add(f1);
    }

    private static boolean isField(Part filePart) {
        return filePart.getSubmittedFileName() == null;
    }

    private static boolean isFile(Part filePart) {
        return !isField(filePart);
    }
}