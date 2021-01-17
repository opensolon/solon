package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudLogger;

/**
 * 云端日志服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudLogService {
    CloudLogger getLogger(String name);
    CloudLogger getLogger(String name, Class<?> clz);
}
