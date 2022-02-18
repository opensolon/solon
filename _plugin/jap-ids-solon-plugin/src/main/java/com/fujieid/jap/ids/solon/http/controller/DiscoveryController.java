package com.fujieid.jap.ids.solon.http.controller;

import com.fujieid.jap.ids.endpoint.DiscoveryEndpoint;
import com.fujieid.jap.ids.model.OidcDiscoveryDto;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;

/**
 * @author 颖
 */
public class DiscoveryController extends BaseController {
    @Get
    @Mapping("openid-configuration")
    public OidcDiscoveryDto openidCfg(){
        return new DiscoveryEndpoint().getOidcDiscoveryInfo(null);
    }

    @Get
    @Mapping("jwks.json")
    public String jwksJson(){
        return new DiscoveryEndpoint().getJwksPublicKey(null);
    }
}
