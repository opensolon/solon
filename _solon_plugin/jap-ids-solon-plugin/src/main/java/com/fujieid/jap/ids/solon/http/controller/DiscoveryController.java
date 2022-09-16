package com.fujieid.jap.ids.solon.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.ids.endpoint.DiscoveryEndpoint;
import com.fujieid.jap.ids.model.OidcDiscoveryDto;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 颖
 * @since 1.6
 */
public class DiscoveryController extends IdsController {
    private final DiscoveryEndpoint discoveryEndpoint = new DiscoveryEndpoint();

    @Get
    @Mapping("openid-configuration")
    public OidcDiscoveryDto openidCfg(HttpServletRequest request){
        return this.discoveryEndpoint.getOidcDiscoveryInfo(
                new JakartaRequestAdapter(request)
        );
    }

    @Get
    @Mapping("jwks.json")
    public String jwksJson(){
        return this.discoveryEndpoint.getJwksPublicKey(null);
    }
}
