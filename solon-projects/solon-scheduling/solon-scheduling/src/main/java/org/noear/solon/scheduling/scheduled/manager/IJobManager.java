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

import org.noear.solon.core.Lifecycle;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.noear.solon.scheduling.scheduled.JobInterceptor;

import java.util.List;
import java.util.Map;

/**
 * @author noear
 * @since 2.2
 */
public interface IJobManager extends Lifecycle {
    /**
     * 添加任务拦截器
     */
    void addJobInterceptor(int index, JobInterceptor jobInterceptor);

    /**
     * 是否有任务拦截器
     */
    boolean hasJobInterceptor();

    /**
     * 获取任务拦截器
     */
    List<RankEntity<JobInterceptor>> getJobInterceptors();

    /**
     * 任务添加
     */
    JobHolder jobAdd(String name, Scheduled scheduled, JobHandler handler);

    /**
     * 任务是否存在
     */
    boolean jobExists(String name);

    /**
     * 任务获取
     */
    JobHolder jobGet(String name);

    /**
     * 任务获取全部
     */
    Map<String, JobHolder> jobGetAll();

    /**
     * 任务移除
     */
    void jobRemove(String name);

    /**
     * 任务开始
     */
    void jobStart(String name, Map<String, String> data) throws ScheduledException;

    /**
     * 任务停止
     */
    void jobStop(String name) throws ScheduledException;

    /**
     * 是否已启动
     */
    boolean isStarted();
}
