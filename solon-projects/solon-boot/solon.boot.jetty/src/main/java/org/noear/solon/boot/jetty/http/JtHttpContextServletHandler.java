package org.noear.solon.boot.jetty.http;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.jetty.XPluginImp;
import org.noear.solon.boot.web.FormUrlencodedUtils;
import org.noear.solon.web.servlet.SolonServletHandler;
import org.noear.solon.core.handle.Context;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JtHttpContextServletHandler extends SolonServletHandler {

    @Override
    protected void preHandle(Context ctx) throws IOException {
        if (ServerProps.output_meta) {
            ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
        }

        //编码窗体预处理
        FormUrlencodedUtils.pretreatment(ctx);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long maxBodySize = (ServerProps.request_maxBodySize > 0 ? ServerProps.request_maxBodySize : -1L);
        long maxFileSize = (ServerProps.request_maxFileSize > 0 ? ServerProps.request_maxFileSize : -1L);

        MultipartConfigElement configElement = new MultipartConfigElement(
                System.getProperty("java.io.tmpdir"),
                maxFileSize,
                maxBodySize,
                0);

        request.setAttribute("org.eclipse.jetty.multipartConfig", configElement);

        super.service(request, response);
    }
}
