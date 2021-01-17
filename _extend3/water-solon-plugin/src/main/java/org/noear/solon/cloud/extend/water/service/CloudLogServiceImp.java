package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.CloudLogger;
import org.noear.solon.cloud.service.CloudLogService;

/**
 * @author noear 2021/1/17 created
 */
public class CloudLogServiceImp implements CloudLogService {
    @Override
    public CloudLogger getLogger(String name) {
        return new CloudLoggerImp(name);
    }

    @Override
    public CloudLogger getLogger(String name, Class<?> clz) {
        return new CloudLoggerImp(name, clz);
    }
}
