package com.fujieid.jap.ids.solon.http.controller;

import com.fujieid.jap.ids.endpoint.DiscoveryEndpoint;
import com.fujieid.jap.ids.model.OidcDiscoveryDto;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;

/**
 * @author 颖
 */
public class DiscoveryController extends IdsController {

    private final DiscoveryEndpoint discoveryEndpoint = new DiscoveryEndpoint();

    @Get
    @Mapping("openid-configuration")
    public OidcDiscoveryDto openidCfg(){
        return this.discoveryEndpoint.getOidcDiscoveryInfo(null);
    }

    @Get
    @Mapping("jwks.json")
    public String jwksJson(){
        return this.discoveryEndpoint.getJwksPublicKey(null);
    }

}
