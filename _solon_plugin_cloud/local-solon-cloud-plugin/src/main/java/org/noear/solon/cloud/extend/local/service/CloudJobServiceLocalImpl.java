package org.noear.solon.cloud.extend.local.service;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.service.CloudJobService;

/**
 * @author noear
 * @since 1.10
 */
public class CloudJobServiceLocalImpl implements CloudJobService {
    @Override
    public boolean register(String name, String cron7x, String description, CloudJobHandler handler) {
        return false;
    }

    @Override
    public boolean isRegistered(String name) {
        return false;
    }
}
