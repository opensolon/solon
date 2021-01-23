package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.CloudLogger;
import org.noear.solon.cloud.service.CloudLogService;

/**
 * @author noear
 * @since 1.2
 */
public class CloudLogServiceImp implements CloudLogService {
    @Override
    public CloudLogger getLogger(String name) {
        return new CloudLoggerImp(name);
    }
}
