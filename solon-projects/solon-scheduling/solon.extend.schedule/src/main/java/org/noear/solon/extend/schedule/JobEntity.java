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


/**
 * 任务实体
 *
 * @author noear
 * @since 1.0
 * */
public class JobEntity {
    private String name;
    private IJob job;

    public JobEntity(String name, IJob job) {
        this(name, job, 0);
    }

    public JobEntity(String name, IJob job, int index) {
        this.job = job;
        this.name = job.getName();

        if (this.name == null) {
            this.name = name;
        }

        if (this.name == null) {
            this.name = job.getClass().getSimpleName();
        }

        if (index > 0) {
            this.name = this.name + index;
        }
    }

    public String getName() {
        return name;
    }

    public IJob getJob() {
        return job;
    }
}