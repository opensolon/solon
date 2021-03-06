package org.noear.solon.cloud.service;

/**
 * 云端哨岗服务
 *
 * @author noear
 * @since 1.3
 */
public interface CloudSentryService {
    /**
     * 进入
     *
     * @param sentryName 哨岗名称
     * */
    boolean entry(String sentryName);
}
