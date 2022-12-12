package org.noear.solon.cloud.extend.quartz;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ContextUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Map;

/**
 * Quartz job 的执行代理
 *
 * @author noear
 * */
public class JobQuartzProxy implements Job {
    @Override
    public void execute(JobExecutionContext jc) throws JobExecutionException {
        String jobId = jc.getJobDetail().getKey().getName();
        CloudJobHandler jobReal = JobManager.getJob(jobId);

        if (jobReal != null) {
            Context ctx = Context.current(); //可能是从上层代理已生成
            if (ctx == null) {
                ctx = new ContextEmpty();
                ContextUtil.currentSet(ctx);
            }

            for (Map.Entry<String, Object> kv : jc.getJobDetail().getJobDataMap().entrySet()) {
                if (kv.getValue() != null) {
                    ctx.attrMap().put(kv.getKey(), kv.getValue());
                    ctx.paramMap().put(kv.getKey(), kv.getValue().toString());
                }
            }

            try {
                jobReal.handle(ctx);
            } catch (Throwable e) {
                throw new JobExecutionException("Job execution failed: " + jobId, e);
            }
        }
    }
}
