package com.fujieid.jap.ids.solon.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.ids.endpoint.LogoutEndpoint;
import com.fujieid.jap.ids.model.IdsResponse;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 颖
 * @since 1.6
 */
public class LogoutController extends IdsController {

    private final LogoutEndpoint logoutEndpoint = new LogoutEndpoint();

    @Get
    @Mapping("logout")
    public void logout(Context ctx, HttpServletRequest request, HttpServletResponse response) {
        IdsResponse<String, String> idsResponse = this.logoutEndpoint.logout(
                new JakartaRequestAdapter(request),
                new JakartaResponseAdapter(response)
        );

        ctx.redirect(idsResponse.getData());
    }
}
