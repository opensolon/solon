package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudLogger;
import org.noear.solon.cloud.service.CloudLogService;

/**
 * @author noear
 * @since 1.2
 */
public class CloudLogServiceImp implements CloudLogService {
    @Override
    public CloudLogger getLogger(String name) {
        return new CloudLoggerSimple(name);
    }

    @Override
    public CloudLogger getLogger(Class<?> clz) {
        return new CloudLoggerSimple(clz);
    }
}
