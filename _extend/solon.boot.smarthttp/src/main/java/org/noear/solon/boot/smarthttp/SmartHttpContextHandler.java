package org.noear.solon.boot.smarthttp;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XMonitor;
import org.noear.solon.core.XPlugin;
import org.smartboot.http.HttpRequest;
import org.smartboot.http.HttpResponse;
import org.smartboot.http.enums.HttpStatus;
import org.smartboot.http.server.handle.HttpHandle;

import java.io.IOException;

public class SmartHttpContextHandler extends HttpHandle {


    @Override
    public void doHandle(HttpRequest request, HttpResponse response) throws IOException {
        /*
         *
         * jlhttp 流程
         *
         * 1.处理，并暂存结果
         * 2.输出头
         * 3.输出内容
         *
         * */

        try {
            SmartHttpContext context = new SmartHttpContext(request, response);

            context.contentType("text/plain;charset=UTF-8");
            if (XServerProp.output_meta) {
                context.headerSet("solon.boot", XPluginImp.solon_boot_ver());
            }

            XApp.global().tryHandle(context);

            if (context.getHandled() && context.status() != 404) {
                context.commit();
            } else {
                context.status(404);
                context.commit();
            }
        } catch (Throwable ex) {
            XMonitor.sendError(null, ex);
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        finally {
            response.close();
        }
    }
}
