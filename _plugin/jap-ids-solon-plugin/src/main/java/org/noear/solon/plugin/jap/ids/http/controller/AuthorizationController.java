package org.noear.solon.plugin.jap.ids.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.ids.endpoint.AuthorizationEndpoint;
import com.fujieid.jap.ids.model.IdsResponse;
import org.noear.solon.SolonApp;
import org.noear.solon.core.handle.Handler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 颖
 */
public class AuthorizationController extends BaseController {

    public AuthorizationController(SolonApp app) {
        // Authorize
        app.get(this.formatPath("/authorize"), ctx -> {
            IdsResponse<String, String> idsResponse = new AuthorizationEndpoint()
                    .authorize(new JakartaRequestAdapter((HttpServletRequest) ctx.request()));
            ctx.redirect(idsResponse.getData());
        });
        // Agree
        Handler agree = ctx -> {
            IdsResponse<String, String> idsResponse = new AuthorizationEndpoint()
                    .agree(new JakartaRequestAdapter((HttpServletRequest) ctx.request()));
            ctx.redirect(idsResponse.getData());
        };
        app.post(this.formatPath("/authorize"), agree);
        // Auto agree
        app.get(this.formatPath("/authorize/auto"), agree);
    }

}
