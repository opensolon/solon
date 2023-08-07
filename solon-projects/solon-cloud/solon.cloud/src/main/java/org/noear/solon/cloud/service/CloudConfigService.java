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
     *
     * @param group 分组
     * @param name 配置名
     */
    Config pull(String group, String name);

    /**
     * 拉取配置
     *
     * @param name 配置名
     */
    default Config pull(String name){
        return pull(Solon.cfg().appGroup(), name);
    }

    /**
     * 推送配置
     *
     * @param group 分组
     * @param name 配置名
     * @param value 值
     */
    boolean push(String group, String name, String value);

    /**
     * 推送配置
     *
     * @param name 配置名
     * @param value 值
     */
    default boolean push(String name, String value) {
        return push(Solon.cfg().appGroup(), name, value);
    }


    /**
     * 移除配置
     *
     * @param group 分组
     * @param name 配置名
     */
    boolean remove(String group, String name);

    /**
     * 移除配置
     *
     * @param name 配置名
     */
    default boolean remove(String name){
        return remove(Solon.cfg().appGroup(), name);
    }

    /**
     * 关注配置
     *
     * @param group 分组
     * @param name 配置名
     * @param observer 观察者
     */
    void attention(String group, String name, CloudConfigHandler observer);
}
