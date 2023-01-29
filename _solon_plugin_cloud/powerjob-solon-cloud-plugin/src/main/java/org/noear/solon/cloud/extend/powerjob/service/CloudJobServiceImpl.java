package org.noear.solon.cloud.extend.powerjob.service;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.extend.powerjob.impl.PowerJobProxy;
import org.noear.solon.cloud.extend.powerjob.JobManager;
import org.noear.solon.cloud.model.JobHolder;
import org.noear.solon.cloud.service.CloudJobService;

/**
 * @author noear
 * @since 2.0
 */
public class CloudJobServiceImpl implements CloudJobService {
    @Override
    public boolean register(String name, String cron7x, String description, CloudJobHandler handler) {
        JobHolder jobHolder = new JobHolder(name, cron7x, description, handler);

        JobManager.addJob(name, new PowerJobProxy(jobHolder));
        return true;
    }

    @Override
    public boolean isRegistered(String name) {
        return JobManager.containsJob(name);
    }
}
