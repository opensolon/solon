package org.noear.solon.boot.wizzardohttp;

import com.wizzardo.http.Handler;
import com.wizzardo.http.HttpConnection;
import com.wizzardo.http.request.Request;
import com.wizzardo.http.response.Response;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XMonitor;

import java.io.IOException;

public class WizzContextHandler implements Handler {
    @Override
    public Response handle(Request<HttpConnection, Response> request, Response response) throws IOException {
        XContext context = new WizzContext(request,response);
        try {
            XApp.global().handle(context);
        }catch (Throwable ex){
            XMonitor.sendError(context,ex);

            context.status(500);
            context.setHandled(true);
            context.output(XUtil.getFullStackTrace(ex));
        }

        return response;
    }
}
