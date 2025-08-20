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
package org.noear.solon.scheduling.simple.test.demo3;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Managed;
import org.noear.solon.scheduling.scheduled.Job;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobInterceptor;

@Slf4j
@Managed(index = 1)
public class JobInterceptor1 implements JobInterceptor {
    @Override
    public void doIntercept(Job job, JobHandler handler) throws Throwable {
        log.warn("111");
        handler.handle(job.getContext());
    }
}
