/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.extend.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 任务运行器默认实现
 *
 * @author noear
 * @since 1.0
 * */
public class JobRunner implements IJobRunner {
    static final Logger log = LoggerFactory.getLogger(JobRunner.class);

    /**
     * 全局运行实例（可以修改替换）
     */
    public static IJobRunner global = new JobRunner();

    /**
     * 是否允许
     */
    public boolean allow(JobEntity jobEntity) {
        return true;
    }

    /**
     * 运行
     *
     * @param jobEntity 任务实体
     * @param tag       标签
     */
    public void run(JobEntity jobEntity, int tag) {
        if (allow(jobEntity)) {
            System.out.print("schedule run::" + jobEntity.getName() + " - " + tag + "\r\n");

            //注：不需要线程池
            new Thread(() -> {
                runDo(jobEntity);
            }, "job-" + jobEntity.getName()).start();
        }
    }

    protected void runDo(JobEntity jobEntity) {
        try {
            if (jobEntity.getJob().getDelay() > 0) {
                //处理延迟
                Thread.sleep(jobEntity.getJob().getDelay());
            }
        } catch (Throwable ee) {
        }

        while (true) {
            try {
                long time_start = System.currentTimeMillis();
                jobEntity.getJob().exec();
                long time_end = System.currentTimeMillis();

                //如果间隔为0，则终止任务
                if (jobEntity.getJob().getInterval() == 0) {
                    return;
                }

                //如果离下次执行还有时间，则休眠一段时间
                if (time_end - time_start < jobEntity.getJob().getInterval()) {
                    Thread.sleep(jobEntity.getJob().getInterval());
                }
            } catch (Throwable e) {
                try {
                    log.warn(e.getMessage(), e);
                    Thread.sleep(1000);
                } catch (Throwable ee) {
                }
            }
        }
    }
}
