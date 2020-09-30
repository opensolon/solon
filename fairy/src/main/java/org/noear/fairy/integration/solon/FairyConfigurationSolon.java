package org.noear.fairy.integration.solon;

import org.noear.fairy.Fairy;
import org.noear.fairy.FairyConfiguration;
import org.noear.fairy.annotation.FairyClient;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XUpstream;

public class FairyConfigurationSolon implements FairyConfiguration {
    @Override
    public void config(FairyClient client, Fairy.Builder builder) {
        if (XUtil.isEmpty(client.value())) {
            return;
        }

        if (client.value().contains("://")) {
            return;
        }

        //upstream name
        String name0 = client.value().split(":")[0];

        //尝试从负载工厂获取
        if (XBridge.upstreamFactory() != null) {
            XUpstream upstream = XBridge.upstreamFactory().create(name0);

            if (upstream != null) {
                builder.upstream(upstream::getServer);
                return;
            }
        }

        //尝试从Ioc容器获取
        Aop.getAsyn(name0, (bw) -> {
            XUpstream tmp = bw.raw();
            builder.upstream(tmp::getServer);
        });
    }
}
