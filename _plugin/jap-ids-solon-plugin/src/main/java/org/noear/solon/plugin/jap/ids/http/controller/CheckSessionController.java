package org.noear.solon.plugin.jap.ids.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.ids.JapIds;
import com.fujieid.jap.ids.model.IdsResponse;
import org.noear.snack.ONode;
import org.noear.solon.SolonApp;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 颖
 */
public class CheckSessionController extends BaseController {

    public CheckSessionController(SolonApp app) {
        app.get(this.formatPath("/check_session"), ctx -> {
            IdsResponse<String, Object> response = new IdsResponse<String, Object>()
                    .data(JapIds.isAuthenticated(new JakartaRequestAdapter((HttpServletRequest) ctx.request())));
            ctx.output(ONode.stringify(response));
        });
    }

}
