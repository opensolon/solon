package org.noear.solon.plugin.jap.ids.http.controller;

import com.fujieid.jap.ids.endpoint.DiscoveryEndpoint;
import org.noear.snack.ONode;
import org.noear.solon.SolonApp;

/**
 * @author 颖
 */
public class DiscoveryController extends BaseController {

    public DiscoveryController(SolonApp app) {
        app.get(this.formatPath("/openid-configuration"), ctx -> {
            ctx.output(ONode.stringify(
                    new DiscoveryEndpoint().getOidcDiscoveryInfo(null)
            ));
        });
        app.get(this.formatPath("/jwks.json"), ctx -> {
            ctx.output(ONode.stringify(
                    new DiscoveryEndpoint().getJwksPublicKey(null)
            ));
        });
    }

    @Override
    protected String formatPath(String path) {
        return "/.well-known";
    }
}
