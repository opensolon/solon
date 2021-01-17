package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudLogger;

/**
 * @author noear 2021/1/17 created
 */
public interface CloudLogService {
    CloudLogger getLogger(String name);
    CloudLogger getLogger(String name, Class<?> clz);
}
