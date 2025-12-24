/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.scheduling.quartz;

import org.noear.solon.core.handle.Context;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

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
            Context ctx = QuartzContext.getContext(jc);

            Context.currentWith(ctx, () -> {
                try {
                    jobHolder.handle(ctx);

                    return null;
                } catch (Throwable e) {
                    throw new JobExecutionException("Job execution failed: " + name, e);
                }
            });
        }
    }
}
