package org.noear.solon.boot.tomcat.ext;


import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MultipartUtil {
    public static List<XFile> getUploadedFiles(XContext context, String partName) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) context.request();

        return request.getParts().stream().filter((it) -> {
            return isFile(it) && it.getName().equals(partName);
        }).map((filePart) -> {
            XFile f = new XFile();
            f.contentType = filePart.getContentType();
            try {
                f.content = filePart.getInputStream(); //可以转成 ByteArrayInputStream
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            /**getPartName start**/
            String cd = filePart.getHeader("Content-Disposition");
            f.name = cd.substring(cd.lastIndexOf("=") + 2, cd.length() - 1);
            /**getPartName end**/
            int idx = f.name.lastIndexOf(".");

            if (idx > 0) {
                f.extension = f.name.substring(idx + 1);
            }

            return f;
        }).collect(Collectors.toList());

    }

    private static boolean isField(Part filePart) {
        String cd = filePart.getHeader("Content-Disposition");
        return cd.substring(cd.lastIndexOf("=") + 2, cd.length() - 1) == null;
    }

    private static boolean isFile(Part filePart) {
        return !isField(filePart);
    }
}
