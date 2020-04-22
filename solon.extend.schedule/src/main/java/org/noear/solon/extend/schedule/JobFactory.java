package org.noear.solon.extend.schedule;


import java.util.HashMap;
import java.util.Map;

public class JobFactory {
    private static Map<String, JobEntity> _jobMap = new HashMap<>();

    private static IJobRunner _runner;

    public static void register(IJob job){
        register(new JobEntity(job.getName(),job));
    }

    public static void register(JobEntity job) {
        if (_jobMap.containsKey(job.getName())) {
            return;
        }

        _jobMap.put(job.getName(), job);

        //如果已有运行器，直接运行
        if (_runner != null) {
            _runner.run(job);
        }
    }

    public static void run(IJobRunner runner) {
        //充许修改一次
        //
        if(_runner != null ){
            return;
        }

        _runner = runner;

        if (_runner != null) {
            //运行一次已存在的任务
            //
            for (JobEntity task : _jobMap.values()) {
                _runner.run(task);
            }
        }
    }
}
