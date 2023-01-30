package org.noear.solon.cloud.extend.powerjob.impl;

import org.noear.solon.cloud.exception.CloudJobException;
import org.noear.solon.cloud.model.JobHolder;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ContextUtil;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

/**
 * PowerJob 代理，用于转 CloudJobHandler 处理
 *
 * @author noear
 * @since 2.0
 */
public class PowerJobProxy implements BasicProcessor {
    private JobHolder jobHolder;

    public PowerJobProxy(JobHolder jobHolder) {
        this.jobHolder = jobHolder;
    }

    @Override
    public ProcessResult process(TaskContext tc) throws Exception {
        if (jobHolder != null) {
            Context ctx = Context.current(); //可能是从上层代理已生成, v1.11
            if (ctx == null) {
                ctx = new ContextEmpty();
                ContextUtil.currentSet(ctx);
            }

            //设置请求对象（mvc 时，可以被注入）
            if (ctx instanceof ContextEmpty) {
                ((ContextEmpty) ctx).request(tc);
            }

            // Long jobId; Long instanceId; Long subInstanceId; String taskId; String taskName;
            // String jobParams; String instanceParams; int maxRetryTimes; int currentRetryTimes;

            ctx.paramMap().put("jobId", String.valueOf(tc.getJobId()));
            ctx.paramMap().put("instanceId", String.valueOf(tc.getInstanceId()));
            ctx.paramMap().put("subInstanceId", String.valueOf(tc.getSubInstanceId()));
            ctx.paramMap().put("taskId", tc.getTaskId());
            ctx.paramMap().put("taskName", tc.getTaskName());
            ctx.paramMap().put("jobParams", tc.getJobParams());
            ctx.paramMap().put("instanceParams", tc.getInstanceParams());
            ctx.paramMap().put("maxRetryTimes", String.valueOf(tc.getMaxRetryTimes()));
            ctx.paramMap().put("currentRetryTimes", String.valueOf(tc.getCurrentRetryTimes()));

            try {
                jobHolder.handle(ctx);

                if (ctx.result instanceof ProcessResult) {
                    return (ProcessResult) ctx.result;
                } else {
                    return new ProcessResult(true);
                }
            } catch (Throwable e) {
                throw new CloudJobException("Job execution failed: " + jobHolder.getName(), e);
            }
        }

        return new ProcessResult(false);
    }
}