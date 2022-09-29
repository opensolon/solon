package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.model.JobHolder;
import org.noear.solon.cloud.service.CloudJobService;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.logging.utils.TagsMDC;
import org.noear.water.WaterClient;
import org.noear.water.model.JobM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 分布式任务服务
 *
 * @author noear
 * @since 1.2
 */
public class CloudJobServiceWaterImp implements CloudJobService {
    public static final CloudJobServiceWaterImp instance = new CloudJobServiceWaterImp();

    public Map<String, JobHolder> jobMap = new LinkedHashMap<>();

    public JobHolder get(String name) {
        return jobMap.get(name);
    }

    public void push() {
        if (jobMap.size() == 0) {
            return;
        }

        List<JobM> jobs = new ArrayList<>();
        jobMap.forEach((k, v) -> {
            jobs.add(new JobM(v.getName(), v.getCron7x(), v.getDescription()));
        });

        try {
            WaterClient.Job.register(Solon.cfg().appGroup(), Solon.cfg().appName(), jobs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean register(String name, String cron7x, String description, CloudJobHandler handler) {
        JobHolder jobHolder = new JobHolder(name, cron7x, description, handler);

        jobMap.put(name, jobHolder);
        TagsMDC.tag0("CloudJob");
        PrintUtil.warn("CloudJob", "Handler registered name:" + name + ", class:" + handler.getClass().getName());
        TagsMDC.tag0("");
        return true;
    }

    @Override
    public boolean isRegistered(String name) {
        return jobMap.containsKey(name);
    }
}
