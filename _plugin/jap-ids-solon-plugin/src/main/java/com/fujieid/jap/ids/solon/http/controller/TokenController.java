package com.fujieid.jap.ids.solon.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.ids.endpoint.TokenEndpoint;
import com.fujieid.jap.ids.model.IdsResponse;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 颖
 */
public class TokenController extends BaseController {
    @Get
    @Mapping("token")
    public Map<String, Object> token(HttpServletRequest request) {
        IdsResponse<String, Object> idsResponse = new TokenEndpoint()
                .getToken(new JakartaRequestAdapter(request));

        return idsResponse;
    }

    @Get
    @Mapping("revoke_token")
    public Map<String, Object> revoke_token(HttpServletRequest request) {
        IdsResponse<String, Object> idsResponse = new TokenEndpoint()
                .revokeToken(new JakartaRequestAdapter(request));

        return idsResponse;
    }
}
