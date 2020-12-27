package org.noear.solon.boot.servlet;

import org.noear.solon.core.handle.UploadedFile;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class MultipartUtil {

    public static void buildParamsAndFiles(ServletContext context) throws IOException, ServletException{
        HttpServletRequest request = (HttpServletRequest) context.request();

        request.setAttribute("org.eclipse.jetty.multipartConfig",
                new MultipartConfigElement(System.getProperty("java.io.tmpdir")));

        for(Part part : request.getParts()){
            if(isFile(part)){
                doBuildFiles(context, part);
            }
        }
    }

    private static void doBuildFiles(ServletContext context, Part part) throws IOException{
        List<UploadedFile> list = context._fileMap.get(part.getName());
        if(list == null){
            list = new ArrayList<>();
            context._fileMap.put(part.getName(), list);


            UploadedFile f = new UploadedFile();
            f.contentType = part.getContentType();
            try {
                f.content = part.getInputStream(); //可以转成 ByteArrayInputStream
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            f.name = part.getSubmittedFileName();
            int idx = f.name.lastIndexOf(".");

            if (idx > 0) {
                f.extension = f.name.substring(idx + 1);
            }

            list.add(f);
        }
    }

    private static boolean isField(Part filePart) {
        return filePart.getSubmittedFileName() == null;
    }

    private static boolean isFile(Part filePart) {
        return !isField(filePart);
    }
}
