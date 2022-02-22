package com.fujieid.jap.ids.solon.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.ids.endpoint.TokenEndpoint;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 颖
 * @since 1.6
 */
public class TokenController extends IdsController {
    private final TokenEndpoint tokenEndpoint = new TokenEndpoint();

    @Get
    @Mapping("token")
    public Map<String, Object> token(HttpServletRequest request) {
        return this.tokenEndpoint
                .getToken(new JakartaRequestAdapter(request));
    }

    @Get
    @Mapping("revoke_token")
    public Map<String, Object> revokeToken(HttpServletRequest request) {
        return this.tokenEndpoint
                .revokeToken(new JakartaRequestAdapter(request));
    }
}
