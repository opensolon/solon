package org.noear.solon.plugin.jap.ids.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.ids.endpoint.LogoutEndpoint;
import com.fujieid.jap.ids.endpoint.TokenEndpoint;
import com.fujieid.jap.ids.model.IdsResponse;
import org.noear.snack.ONode;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 颖
 */
public class TokenController extends BaseController {

    public TokenController(SolonApp app) {
        app.get(this.formatPath("/token"), ctx -> {
           IdsResponse<String, Object> idsResponse = new TokenEndpoint()
                   .getToken(new JakartaRequestAdapter((HttpServletRequest) ctx.request()));

            ctx.output(ONode.stringify(idsResponse));
        });
        app.get(this.formatPath("/revoke_token"), ctx -> {
           IdsResponse<String, Object> idsResponse = new TokenEndpoint()
                   .revokeToken(new JakartaRequestAdapter((HttpServletRequest) ctx.request()));

            ctx.output(ONode.stringify(idsResponse));
        });
    }

}
