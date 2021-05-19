package org.noear.solon.cloud.service;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;

/**
 * 云端配置服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudConfigService {
    /**
     * 拉取配置
     */
    Config pull(String group, String key);

    default Config pull(String key){
        return pull(Solon.cfg().appGroup(), key);
    }

    /**
     * 推送配置
     */
    boolean push(String group, String key, String value);

    default boolean push(String key, String value) {
        return push(Solon.cfg().appGroup(), key, value);
    }


    /**
     * 移除配置
     */
    boolean remove(String group, String key);

    default boolean remove(String key){
        return remove(Solon.cfg().appGroup(), key);
    }

    /**
     * 关注配置
     */
    void attention(String group, String key, CloudConfigHandler observer);
}
