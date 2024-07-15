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
