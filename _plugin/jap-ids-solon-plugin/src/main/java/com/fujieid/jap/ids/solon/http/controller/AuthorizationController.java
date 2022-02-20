package com.fujieid.jap.ids.solon.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.ids.endpoint.AuthorizationEndpoint;
import com.fujieid.jap.ids.model.IdsResponse;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author 颖
 * @since 1.6
 */
public class AuthorizationController extends IdsController {

    private final AuthorizationEndpoint authorizationEndpoint = new AuthorizationEndpoint();

    @Get
    @Mapping("authorize")
    public void authorizeGet(Context ctx, HttpServletRequest request) throws IOException {
        //authorize
        IdsResponse<String, String> idsResponse = this.authorizationEndpoint
                .authorize(new JakartaRequestAdapter(request));

        ctx.redirect(idsResponse.getData());
    }

    @Post
    @Mapping("authorize")
    public void authorizePost(Context ctx, HttpServletRequest request) throws IOException {
        //agree
        IdsResponse<String, String> idsResponse = this.authorizationEndpoint
                .agree(new JakartaRequestAdapter(request));

        ctx.redirect(idsResponse.getData());
    }

    @Get
    @Mapping("authorize/auto")
    public void authorizeAuto(Context ctx, HttpServletRequest request) throws IOException {
        //Auto agree
        this.authorizePost(ctx, request);
    }

}
