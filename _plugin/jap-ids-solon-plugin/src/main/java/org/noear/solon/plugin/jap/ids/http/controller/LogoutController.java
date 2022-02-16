package org.noear.solon.plugin.jap.ids.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.ids.endpoint.LogoutEndpoint;
import com.fujieid.jap.ids.model.IdsResponse;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 颖
 */
public class LogoutController extends BaseController {

    public LogoutController(SolonApp app) {
        app.get(this.formatPath("/logout"), ctx -> {
            IdsResponse<String, String> idsResponse = new LogoutEndpoint().logout(
                    new JakartaRequestAdapter((HttpServletRequest) ctx.request()),
                    new JakartaResponseAdapter((HttpServletResponse) ctx.response())
            );
            ctx.redirect(idsResponse.getData());
        });
    }

}
