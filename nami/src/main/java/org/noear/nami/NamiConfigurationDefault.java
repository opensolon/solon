package org.noear.nami;

import org.noear.nami.annotation.NamiClient;

/**
 * Fairy - 默认配置器
 *
 * @author noear
 * @since 1.0
 * */
public class NamiConfigurationDefault implements NamiConfiguration {
    public static NamiConfiguration proxy;

    @Override
    public void config(NamiClient client, Nami.Builder builder) {
        if (proxy != null) {
            proxy.config(client, builder);
        }
    }
}
