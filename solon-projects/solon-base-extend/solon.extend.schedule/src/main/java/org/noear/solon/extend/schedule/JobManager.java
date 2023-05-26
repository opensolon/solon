package org.noear.solon.extend.schedule;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务管理器
 *
 * @author noear
 * @since 1.0
 * */
public class JobManager {
    private static Map<String, JobEntity> _jobMap = new HashMap<>();

    private static IJobRunner _runner;

    /**
     * 注册任务
     *
     * @param job 任务实体
     * */
    protected static void register(JobEntity job) {
        if (_jobMap.containsKey(job.getName())) {
            return;
        }

        _jobMap.put(job.getName(), job);

        //如果已有运行器，直接运行
        if (_runner != null) {
            runDo(job);
        }
    }

    /**
     * 运行任务
     *
     * @param runner 运行器
     * */
    protected static void run(IJobRunner runner) {
        //充许修改一次
        //
        if (_runner != null) {
            return;
        }

        _runner = runner;

        if (_runner != null) {
            //运行一次已存在的任务
            //
            for (JobEntity job : _jobMap.values()) {
                runDo(job);
            }
        }
    }

    private static void runDo(JobEntity job) {
        for (int i = 0; i < job.getJob().getThreads(); i++) {
            _runner.run(job, i);
        }
    }
}
