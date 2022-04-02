package org.noear.solon.cloud.extend.xxljob.service;

import com.xxl.job.core.executor.XxlJobExecutor;
import org.noear.solon.cloud.model.JobHolder;
import org.noear.solon.cloud.service.CloudJobService;
import org.noear.solon.core.handle.Handler;

/**
 * @author noear
 * @since 1.4
 */
public class CloudJobServiceImpl implements CloudJobService {
    public static final CloudJobService instance = new CloudJobServiceImpl();

    @Override
    public boolean register(String name, String cron7x, String description, Handler handler) {
        JobHolder jobHolder = new JobHolder(name, cron7x, description, handler);

        XxlJobExecutor.registJobHandler(name, new IJobHandlerImpl(jobHolder));
        return true;
    }

    @Override
    public boolean isRegistered(String name) {
        return XxlJobExecutor.loadJobHandler(name) != null;
    }
}
