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
package org.noear.solon.scheduling.scheduled.manager;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.scheduling.ScheduledAnno;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.proxy.JobHandlerBeanProxy;
import org.noear.solon.scheduling.scheduled.proxy.JobHandlerMethodProxy;
import org.noear.solon.scheduling.utils.ScheduledHelper;

import java.lang.reflect.Method;

/**
 * 任务提取器
 *
 * @author noear
 * @since 1.11
 * @since 2.2
 */
public class JobExtractor implements BeanBuilder<Scheduled>, BeanExtractor<Scheduled> {
    private final IJobManager jobManager;

    public JobExtractor(IJobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, Scheduled anno) throws Throwable {
        if (bw.raw() instanceof Runnable || bw.raw() instanceof JobHandler) {
            ScheduledAnno warpper = new ScheduledAnno(anno);

            ScheduledHelper.configScheduled(warpper);

            JobHandler handler = new JobHandlerBeanProxy(bw);

            String name= warpper.name();
            String simpleName = warpper.name();
            if (Utils.isEmpty(name)) {
                name = bw.clz().getName();
                simpleName = bw.clz().getSimpleName();
            }

            jobManager.jobAdd(name, warpper, handler).simpleName(simpleName);
        } else {
            throw new IllegalStateException("Job only supports Runnable or JobHandler types!");
        }
    }

    @Override
    public void doExtract(BeanWrap bw, Method method, Scheduled anno) throws Throwable {
        ScheduledAnno warpper = new ScheduledAnno(anno);

        ScheduledHelper.configScheduled(warpper);

        JobHandler handler = new JobHandlerMethodProxy(bw, method);

        String name = warpper.name();
        String simpleName = warpper.name();
        if (Utils.isEmpty(name)) {
            name = bw.clz().getName() + "::" + method.getName();
            simpleName = method.getName();
        }

        jobManager.jobAdd(name, warpper, handler).simpleName(simpleName);
    }
}