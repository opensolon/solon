package org.noear.solon.plugin.jap.ids.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.ids.endpoint.ApprovalEndpoint;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 颖
 */
@Component
public class ApprovalController extends BaseController {

    public ApprovalController(SolonApp app) {
        app.get(this.formatPath("/confirm"), ctx -> {
            ApprovalEndpoint approvalEndpoint = new ApprovalEndpoint();
            approvalEndpoint.showConfirmPage(
                    new JakartaRequestAdapter((HttpServletRequest) ctx.request()),
                    new JakartaResponseAdapter((HttpServletResponse) ctx.response())
            );
        });
    }

}
