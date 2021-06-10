package org.noear.nami.integration.solon;

import org.noear.nami.*;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.LoadBalance;
import org.noear.solon.core.event.EventBus;

/**
 * @author noear
 * @since 1.2
 * */
public final class NamiConfigurationSolon implements NamiConfiguration {

    private NamiConfiguration custom;

    public NamiConfigurationSolon() {
        //
        //如果有定制的NamiConfiguration, 则用之
        //
        Aop.getAsyn(NamiConfiguration.class, (bw) -> {
            custom = bw.raw();
        });

        //订阅拦截器
        EventBus.subscribe(Filter.class, it -> {
            NamiManager.reg(it);
        });
    }

    @Override
    public void config(NamiClient client, NamiBuilder builder) {
        if (Utils.isEmpty(client.name())) {
            return;
        }

        //尝试自定义
        if (custom != null) {
            custom.config(client, builder);
        }

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
