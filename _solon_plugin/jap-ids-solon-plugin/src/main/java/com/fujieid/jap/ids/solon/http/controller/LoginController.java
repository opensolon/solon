package com.fujieid.jap.ids.solon.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.ids.endpoint.LoginEndpoint;
import com.fujieid.jap.ids.model.IdsResponse;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 颖
 * @since 1.6
 */
public class LoginController extends IdsController {
    private final LoginEndpoint loginEndpoint = new LoginEndpoint();

    @Get
    @Mapping("login")
    public void loginGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.loginEndpoint.showLoginPage(
                new JakartaRequestAdapter(request),
                new JakartaResponseAdapter(response)
        );
    }

    @Post
    @Mapping("login")
    public void loginPost(Context ctx, HttpServletRequest request, HttpServletResponse response) {
        IdsResponse<String, String> idsResponse = this.loginEndpoint.signin(
                new JakartaRequestAdapter(request),
                new JakartaResponseAdapter(response)
        );
        ctx.redirect(idsResponse.getData());
    }
}
