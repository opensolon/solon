package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudLogger;

/**
 * 云端日志服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudLogService {
    /**
     * 获取日志器
     * */
    CloudLogger getLogger(String name);

    /**
     * 获取日志器
     * */
    CloudLogger getLogger(String name, Class<?> clz);
}
