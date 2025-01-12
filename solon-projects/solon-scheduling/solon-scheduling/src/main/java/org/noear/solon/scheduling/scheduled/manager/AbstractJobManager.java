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

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.noear.solon.scheduling.scheduled.JobInterceptor;

import java.util.*;

/**
 * 任务管理者
 *
 * @author noear
 * @since 2.2
 */
public abstract class AbstractJobManager implements IJobManager {

    protected final Map<String, JobHolder> jobMap = new HashMap<>();
    protected final List<RankEntity<JobInterceptor>> jobInterceptorNodes = new ArrayList<>();

    public AbstractJobManager() {
        Solon.context().subWrapsOfType(JobInterceptor.class, bw -> {
            addJobInterceptor(bw.index(), bw.raw());
        });
    }

    /**
     * 添加拦截器
     */
    @Override
    public void addJobInterceptor(int index, JobInterceptor jobInterceptor) {
        jobInterceptorNodes.add(new RankEntity<>(jobInterceptor, index));
        jobInterceptorNodes.sort(Comparator.comparingInt(f -> f.index));
    }

    /**
     * 是否有任务拦截器
     */
    @Override
    public boolean hasJobInterceptor() {
        return jobInterceptorNodes.size() > 0;
    }

    /**
     * 拦截器
     */
    @Override
    public List<RankEntity<JobInterceptor>> getJobInterceptors() {
        return Collections.unmodifiableList(jobInterceptorNodes);
    }


    /**
     * 任务添加
     */
    @Override
    public JobHolder jobAdd(String name, Scheduled scheduled, JobHandler handler) {
        jobAddCheckDo(name, scheduled);

        JobHolder jobHolder;
        if (jobExists(name)) {
            jobHolder = jobMap.get(name);
        } else {
            jobHolder = jobWrapDo(name, scheduled, handler);
            jobMap.put(name, jobHolder);
        }

        if (isStarted()) {
            //如果启动，则直接运行
            jobStart(name, null);
        }

        return jobHolder;
    }

    /**
     * 任务包装
     */
    protected JobHolder jobWrapDo(String name, Scheduled scheduled, JobHandler handler) {
        return new JobHolder(this, name, scheduled, handler);
    }

    /**
     * 任务添加检测
     */
    protected void jobAddCheckDo(String name, Scheduled scheduled) {
        if (Utils.isEmpty(name)) {
            //不能没有名字
            throw new IllegalArgumentException("The job name cannot be empty!");
        }

        if (scheduled.fixedDelay() > 0 && scheduled.fixedRate() > 0) {
            if (Utils.isEmpty(scheduled.cron())) {
                //不能同时有 fixedDelay 和 fixedRate
                throw new IllegalArgumentException("The job fixedDelay and fixedRate cannot both have values: " + name);
            } else {
                //不能再有 cron
                throw new IllegalArgumentException("The job cron and fixedDelay and fixedRate cannot both have values: " + name);
            }
        }
    }

    /**
     * 任务是否存在
     */
    @Override
    public boolean jobExists(String name) {
        return jobMap.containsKey(name);
    }


    /**
     * 任务获取
     */
    @Override
    public JobHolder jobGet(String name) {
        return jobMap.get(name);
    }

    /**
     * 任务获取全部
     */
    @Override
    public Map<String, JobHolder> jobGetAll() {
        return Collections.unmodifiableMap(jobMap);
    }

    /**
     * 任务移除
     */
    @Override
    public void jobRemove(String name) throws ScheduledException {
        jobStop(name);
        jobMap.remove(name);
    }


    protected boolean isStarted = false;

    /**
     * 是否已启动
     */
    @Override
    public boolean isStarted() {
        return isStarted;
    }
}
