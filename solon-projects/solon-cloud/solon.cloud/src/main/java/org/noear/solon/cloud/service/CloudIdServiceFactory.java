package org.noear.solon.cloud.service;

import org.noear.solon.Solon;

/**
 * 云端Id服务工厂
 *
 * @author noear
 * @since 1.3
 */
@FunctionalInterface
public interface CloudIdServiceFactory {

    /**
     * 创建云端Id服务
     *
     * @param group 分组
     * @param service 服务名
     * */
    CloudIdService create(String group, String service);


    /**
     * 创建云端Id服务
     * */
    default CloudIdService create(){
        return create(Solon.cfg().appGroup(), Solon.cfg().appName());
    }
}
