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
package org.noear.solon.scheduling.simple;

import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.noear.solon.scheduling.scheduled.manager.AbstractJobManager;

import java.util.Map;

/**
 * 任务管理器
 *
 * @author noear
 * @since 1.6
 */
public class JobManager extends AbstractJobManager {
    private static JobManager instance = new JobManager();

    /**
     * 获取实例
     */
    public static JobManager getInstance() {
        return instance;
    }


    @Override
    protected JobHolder jobWrapDo(String name, Scheduled scheduled, JobHandler handler) {
        JobHolder jobHolder = super.jobWrapDo(name, scheduled, handler);
        jobHolder.setAttachment(new SimpleScheduler(jobHolder));

        return jobHolder;
    }

    @Override
    public void jobStart(String name, Map<String, String> data) throws ScheduledException {
        JobHolder jobHolder = jobGet(name);

        if (jobHolder != null) {
            jobHolder.setData(data);

            try {
                ((SimpleScheduler) jobHolder.getAttachment()).start();
            } catch (Throwable e) {
                throw new ScheduledException(e);
            }
        }
    }

    @Override
    public void jobStop(String name) throws ScheduledException {
        JobHolder jobHolder = jobGet(name);

        if (jobHolder != null) {
            try {
                ((SimpleScheduler) jobHolder.getAttachment()).stop();
            } catch (Throwable e) {
                throw new ScheduledException(e);
            }
        }
    }

    @Override
    public void start() throws Throwable {
        for (JobHolder holder : jobMap.values()) {
            if (holder.getScheduled().enable()) {
                //只启动启用的（如果有需要，手动启用）
                ((SimpleScheduler) holder.getAttachment()).start();
            }
        }

        isStarted = true;
    }

    @Override
    public void stop() throws Throwable {
        isStarted = false;

        for (JobHolder jobHolder : jobMap.values()) {
            ((SimpleScheduler) jobHolder.getAttachment()).stop();
        }
    }
}