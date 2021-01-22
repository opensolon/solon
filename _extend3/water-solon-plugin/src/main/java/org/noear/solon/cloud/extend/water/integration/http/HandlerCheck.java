package org.noear.solon.cloud.extend.water.integration.http;

import org.noear.snack.ONode;
import org.noear.solon.cloud.impl.CloudLoadBalance;
import org.noear.solon.cloud.impl.CloudLoadBalanceFactory;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.water.utils.TextUtils;

/**
 * @author noear
 * @since 1.2
 */
public class HandlerCheck implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        ctx.outputAsJson(handle0(ctx));
    }

    private String handle0(Context ctx) {
        String ups = ctx.param("upstream");

        if (TextUtils.isEmpty(ups) == false) {
            //用于检查负责的情况
            ONode odata = new ONode().asObject();


            if ("*".equals(ups)) {
                CloudLoadBalanceFactory.instance.forEach((k, v) -> {
                    ONode n = odata.get(k);

                    n.set("service", k);
                    ONode nl = n.get("upstream").asArray();
                    v.getDiscovery().cluster().forEach((s) -> {
                        nl.add(s.address());
                    });
                });
            } else {
                CloudLoadBalance v = CloudLoadBalanceFactory.instance.get(ups);
                if (v != null) {
                    Discovery d = v.getDiscovery();
                    if (d != null) {
                        ONode n = odata.get(ups);

                        n.set("service", ups);
                        n.set("agent", d.agent());
                        n.set("policy", d.policy());
                        ONode nl = n.get("upstream").asArray();
                        d.cluster().forEach((s) -> {
                            nl.add(s.address());
                        });
                    }
                }
            }

            return odata.toJson();
        }

        return "{\"code\":1}";
    }
}
