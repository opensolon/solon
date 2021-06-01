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
    Config pull(String group, String name);

    default Config pull(String name){
        return pull(Solon.cfg().appGroup(), name);
    }

    /**
     * 推送配置
     */
    boolean push(String group, String name, String value);

    default boolean push(String name, String value) {
        return push(Solon.cfg().appGroup(), name, value);
    }


    /**
     * 移除配置
     */
    boolean remove(String group, String name);

    default boolean remove(String name){
        return remove(Solon.cfg().appGroup(), name);
    }

    /**
     * 关注配置
     */
    void attention(String group, String name, CloudConfigHandler observer);
}
