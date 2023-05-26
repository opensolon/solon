package org.noear.solon.cloud.service;

/**
 * 云端Id服务
 *
 * @author noear
 * @since 1.3
 */
public interface CloudIdService {
    /**
     * 生成
     *
     * @return Id
     * */
    long generate();
}
