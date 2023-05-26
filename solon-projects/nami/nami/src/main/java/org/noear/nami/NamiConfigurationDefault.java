package org.noear.nami;

import org.noear.nami.annotation.NamiClient;

/**
 * Nami - 默认配置器
 *
 * @author noear
 * @since 1.0
 * */
public final class NamiConfigurationDefault implements NamiConfiguration {
    public static NamiConfiguration proxy;

    @Override
    public void config(NamiClient client, NamiBuilder builder) {
        if (proxy != null) {
            proxy.config(client, builder);
        }
    }
}
