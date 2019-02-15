package org.noear.solon.boot.jetty.jsp;


import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

class MultipartUtil {
    public static List<XFile> getUploadedFiles(XContext context, String partName) throws IOException, ServletException{
        HttpServletRequest request = (HttpServletRequest)context.request();

        request.setAttribute("org.eclipse.jetty.multipartConfig",
                new MultipartConfigElement(System.getProperty("java.io.tmpdir")));

        return request.getParts().stream().filter((it) -> {
            return isFile(it) && it.getName().equals(partName);
        }).map((filePart) -> {
            XFile f = new XFile();
            f.contentType = filePart.getContentType();
            try {
                f.content = filePart.getInputStream(); //可以转成 ByteArrayInputStream
            }catch (Exception ex){
                ex.printStackTrace();
            }

            f.name = filePart.getSubmittedFileName();
            int idx = f.name.lastIndexOf(".");

            if (idx > 0) {
                f.extension = f.name.substring(idx + 1);
            }

            return f;
        }).collect(Collectors.toList());

    }

    private static boolean isField(Part filePart){
        return filePart.getSubmittedFileName() == null;
    }

    private static boolean isFile(Part filePart){
        return !isField(filePart);
    }
}
