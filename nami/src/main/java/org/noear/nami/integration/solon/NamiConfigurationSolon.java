package org.noear.nami.integration.solon;

import org.noear.nami.Nami;
import org.noear.nami.NamiConfiguration;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.LoadBalance;

public class NamiConfigurationSolon implements NamiConfiguration {
    @Override
    public void config(NamiClient client, Nami.Builder builder) {
        if (Utils.isEmpty(client.name())) {
            return;
        }

        //设置调试模式
        builder.debug(Solon.cfg().isDebugMode() || Solon.cfg().isFilesMode());

        //尝试从负载工厂获取
        if (Bridge.upstreamFactory() != null) {
            LoadBalance upstream = Bridge.upstreamFactory().create(client.group(), client.name());

            if (upstream != null) {
                builder.upstream(upstream::getServer);
                return;
            }
        }

        //尝试从Ioc容器获取
        Aop.getAsyn(client.name(), (bw) -> {
            LoadBalance tmp = bw.raw();
            builder.upstream(tmp::getServer);
        });
    }
}
