package org.noear.solon.scheduling.quartz;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ContextUtil;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Map;

/**
 * Quartz 任务执行代理
 *
 * @author noear
 * @since 1.11
 * @since 2.2
 * */
public class QuartzProxy implements Job {
    @Override
    public void execute(JobExecutionContext jc) throws JobExecutionException {
        String name = jc.getJobDetail().getKey().getName();
        JobHolder jobHolder = JobManager.getInstance().jobGet(name);

        if (jobHolder != null) {
            Context ctx = Context.current(); //可能是从上层代理已生成, v1.11
            if (ctx == null) {
                ctx = new ContextEmpty();
                ContextUtil.currentSet(ctx);
            }

            //设置请求对象（mvc 时，可以被注入）
            if(ctx instanceof ContextEmpty) {
                ((ContextEmpty) ctx).request(jc);
            }

            for (Map.Entry<String, Object> kv : jc.getJobDetail().getJobDataMap().entrySet()) {
                if (kv.getValue() != null) {
                    ctx.paramMap().put(kv.getKey(), kv.getValue().toString());
                }
            }

            try {
                jobHolder.setContext(ctx);
                jobHolder.handle();
            } catch (Throwable e) {
                throw new JobExecutionException("Job execution failed: " + name, e);
            }
        }
    }
}
