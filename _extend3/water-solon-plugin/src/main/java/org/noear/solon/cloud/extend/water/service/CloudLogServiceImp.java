package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.CloudLogger;
import org.noear.solon.cloud.service.CloudLogService;

/**
 * @author noear
 * @since 1.2
 */
public class CloudLogServiceImp implements CloudLogService {
    public CloudLogServiceImp(){

    }

    @Override
    public CloudLogger getLogger(String name, Class<?> clz) {
        return new CloudLoggerImp(name, clz);
    }
}
