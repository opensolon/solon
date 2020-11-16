package org.noear.fairy.integration.solon;

import org.noear.fairy.Fairy;
import org.noear.fairy.FairyConfiguration;
import org.noear.fairy.annotation.FairyClient;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Upstream;

public class FairyConfigurationSolon implements FairyConfiguration {
    @Override
    public void config(FairyClient client, Fairy.Builder builder) {
        if (Utils.isEmpty(client.value())) {
            return;
        }

        if (client.value().contains("://")) {
            return;
        }

        //upstream name
        String name0 = client.value().split(":")[0];

        //尝试从负载工厂获取
        if (Bridge.upstreamFactory() != null) {
            Upstream upstream = Bridge.upstreamFactory().create(name0);

            if (upstream != null) {
                builder.upstream(upstream::getServer);
                return;
            }
        }

        //尝试从Ioc容器获取
        Aop.getAsyn(name0, (bw) -> {
            Upstream tmp = bw.raw();
            builder.upstream(tmp::getServer);
        });
    }
}
