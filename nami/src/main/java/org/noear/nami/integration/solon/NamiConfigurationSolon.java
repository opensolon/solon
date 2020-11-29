package org.noear.nami.integration.solon;

import org.noear.nami.Nami;
import org.noear.nami.NamiConfiguration;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.LoadBalance;

public class NamiConfigurationSolon implements NamiConfiguration {
    @Override
    public void config(NamiClient client, Nami.Builder builder) {
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
            LoadBalance upstream = Bridge.upstreamFactory().create(name0);

            if (upstream != null) {
                builder.upstream(upstream::getServer);
                return;
            }
        }

        //尝试从Ioc容器获取
        Aop.getAsyn(name0, (bw) -> {
            LoadBalance tmp = bw.raw();
            builder.upstream(tmp::getServer);
        });
    }
}
