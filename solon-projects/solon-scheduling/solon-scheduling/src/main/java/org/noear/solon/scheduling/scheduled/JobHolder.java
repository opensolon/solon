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
package org.noear.solon.scheduling.scheduled;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.manager.IJobManager;
import org.noear.solon.scheduling.scheduled.impl.JobImpl;

import java.util.Map;

/**
 * 任务持有人
 *
 * @author noear
 * @since 2.2
 */
public class JobHolder implements JobHandler {
    protected final String name;
    protected final Scheduled scheduled;
    protected final JobHandler handler;
    protected final IJobManager jobManager;
    protected String simpleName;

    protected Map<String, String> data;
    protected Object attachment;

    public JobHolder(IJobManager jobManager, String name, Scheduled scheduled, JobHandler handler) {
        this.name = name;
        this.scheduled = scheduled;
        this.handler = handler;
        this.jobManager = jobManager;
    }

    public JobHolder simpleName(String simpleName){
        this.simpleName = simpleName;
        return this;
    }

    public String getSimpleName() {
        return simpleName;
    }

    /**
     * 获取名字
     */
    public String getName() {
        return name;
    }

    /**
     * 获取计划
     */
    public Scheduled getScheduled() {
        return scheduled;
    }

    /**
     * 获取处理器
     */
    public JobHandler getHandler() {
        return handler;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public Map<String, String> getData() {
        return data;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    /**
     * 处理
     */
    @Override
    public void handle(Context ctx) throws Throwable {
        if (ctx == null) {
            ctx = new ContextEmpty();
        }

        if (data != null) {
            ctx.paramMap().putAll(data);
        }

        if (jobManager.hasJobInterceptor()) {
            Job job = new JobImpl(this, ctx);
            JobHandlerChain handlerChain = new JobHandlerChain(job, handler, jobManager.getJobInterceptors());
            handlerChain.handle(ctx);
        } else {
            handler.handle(ctx);
        }
    }
}
