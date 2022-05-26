package org.noear.solon.cloud.extend.water.integration.http;

import org.noear.snack.ONode;
import org.noear.solon.cloud.impl.CloudLoadBalance;
import org.noear.solon.cloud.impl.CloudLoadBalanceFactory;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.water.utils.TextUtils;

/**
 * 状态检测处理 //较高频
 *
 * @author noear
 * @since 1.2
 */
public class HandlerCheck implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        ctx.outputAsJson(handle0(ctx));
    }

    private String handle0(Context ctx) {
        String service = ctx.param("upstream");

        if (TextUtils.isEmpty(service) == false) {
            //用于检查负责的情况
            ONode odata = new ONode().asObject();

            if ("*".equals(service)) {
                CloudLoadBalanceFactory.instance.forEach((k, v) -> {
                    ONode n = odata.getOrNew(k);

                    n.set("service", k);

                    Discovery d = v.getDiscovery();
                    if (d != null) {
                        n.set("agent", d.agent());
                        n.set("policy", d.policy());

                        ONode nl = n.getOrNew("upstream").asArray();

                        d.cluster().forEach((s) -> {
                            nl.add(s.uri());
                        });
                    }
                });
            } else {
                ONode n = odata.getOrNew(service);
                n.set("service", service);

                CloudLoadBalance v = CloudLoadBalanceFactory.instance.get("",service);

                if (v != null) {
                    Discovery d = v.getDiscovery();
                    if (d != null) {
                        n.set("agent", d.agent());
                        n.set("policy", d.policy());

                        ONode nl = n.getOrNew("upstream").asArray();

                        d.cluster().forEach((s) -> {
                            nl.add(s.uri());
                        });
                    }
                }
            }

            return odata.toJson();
        }

        return "{\"code\":1}";
    }
}
