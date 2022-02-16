package org.noear.solon.plugin.jap.ids.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.ids.endpoint.TokenEndpoint;
import com.fujieid.jap.ids.endpoint.UserInfoEndpoint;
import com.fujieid.jap.ids.model.IdsResponse;
import org.noear.snack.ONode;
import org.noear.solon.SolonApp;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 颖
 */
public class UserController extends BaseController {

    public UserController(SolonApp app) {
        app.get(this.formatPath("/userInfo"), ctx -> {
            IdsResponse<String, Object> idsResponse = new UserInfoEndpoint()
                    .getCurrentUserInfo(new JakartaRequestAdapter((HttpServletRequest) ctx.request()));

            ctx.output(ONode.stringify(idsResponse));
        });
    }

}
