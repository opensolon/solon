package org.noear.solon.cloud.service;


/**
 * 云端事件加强服务（事件总线服务）
 *
 * @author noear
 * @since 1.5
 */
public interface CloudEventServicePlus extends CloudEventService{
    /**
     * 获取通道配置
     * */
    String getChannel();

    /**
     * 获取默认分组配置
     * */
    String getGroup();
}
