package org.noear.fairy;

import org.noear.fairy.annotation.FairyClient;

/**
 * Fairy - 配置器
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface FairyConfiguration {
    /**
     * 配置客户端
     * */
    void config(FairyClient client, Fairy.Builder builder);
}
