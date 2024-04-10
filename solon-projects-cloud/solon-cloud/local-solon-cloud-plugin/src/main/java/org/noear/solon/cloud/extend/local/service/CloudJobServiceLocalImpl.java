package org.noear.solon.cloud.extend.local.service;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.exception.CloudJobException;
import org.noear.solon.cloud.extend.local.impl.job.CloudJobRunnable;
import org.noear.solon.cloud.extend.local.impl.job.Cron7X;
import org.noear.solon.cloud.extend.local.impl.job.JobManager;
import org.noear.solon.cloud.model.JobHolder;
import org.noear.solon.cloud.service.CloudJobService;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.logging.utils.TagsMDC;

import java.text.ParseException;

/**
 * 云端定时任务（本地摸拟实现）
 *
 * @author noear
 * @since 1.11
 */
public class CloudJobServiceLocalImpl implements CloudJobService {
    @Override
    public boolean register(String name, String cron7x, String description, CloudJobHandler handler) {
        try {
            JobHolder jobHolder = new JobHolder(name, cron7x, description, handler);
            Cron7X cron7X = Cron7X.parse(cron7x);

            if (cron7X.getInterval() == null) {
                JobManager.add(name, description, cron7x, new CloudJobRunnable(jobHolder));
            } else {
                JobManager.add(name, description, cron7X.getInterval(), new CloudJobRunnable(jobHolder));
            }

            TagsMDC.tag0("CloudJob");
            LogUtil.global().info("CloudJob: Handler registered name:" + name + ", class:" + handler.getClass().getName());
            TagsMDC.tag0("");

            return true;
        } catch (ParseException e) {
            throw new CloudJobException(e);
        }
    }

    @Override
    public boolean isRegistered(String name) {
        return JobManager.contains(name);
    }
}
