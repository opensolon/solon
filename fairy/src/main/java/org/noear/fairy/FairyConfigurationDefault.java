package org.noear.fairy;

import org.noear.fairy.annotation.FairyClient;

/**
 * Fairy - 默认配置器
 *
 * @author noear
 * @since 1.0
 * */
public class FairyConfigurationDefault implements FairyConfiguration {
    public static FairyConfiguration proxy;

    @Override
    public void config(FairyClient client, Fairy.Builder builder) {
        if (proxy != null) {
            proxy.config(client, builder);
        }
    }
}
