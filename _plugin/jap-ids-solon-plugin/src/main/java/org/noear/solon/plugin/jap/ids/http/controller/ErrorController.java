package org.noear.solon.plugin.jap.ids.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.ids.endpoint.ErrorEndpoint;
import org.noear.solon.SolonApp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 颖
 */
public class ErrorController extends BaseController {

    public ErrorController(SolonApp app) {
        app.get(this.formatPath("/error"), ctx -> {
            new ErrorEndpoint().showErrorPage(
                    new JakartaRequestAdapter((HttpServletRequest) ctx.request()),
                    new JakartaResponseAdapter((HttpServletResponse) ctx.response())
            );
        });
    }

}
