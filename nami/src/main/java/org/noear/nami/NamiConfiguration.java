package org.noear.nami;

import org.noear.nami.annotation.NamiClient;

/**
 * Fairy - 配置器
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface NamiConfiguration {
    /**
     * 配置客户端
     * */
    void config(NamiClient client, Nami.Builder builder);
}
