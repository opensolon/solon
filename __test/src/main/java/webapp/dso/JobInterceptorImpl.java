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
package webapp.dso;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.logging.utils.TagsMDC;
import org.noear.solon.scheduling.scheduled.Job;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobInterceptor;

/**
 * @author noear 2024/2/28 created
 */
@Slf4j
@Component
public class JobInterceptorImpl implements JobInterceptor {
    @Override
    public void doIntercept(Job job, JobHandler handler) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            handler.handle(job.getContext());
        } catch (Throwable e) {
            //记录日志
            TagsMDC.tag0("job");
            TagsMDC.tag1(job.getName());
            log.error("{}", e);

            throw e; //别吃掉
        } finally {
            //记录一个内部处理的花费时间
            long timespan = System.currentTimeMillis() - start;
            System.out.println("JobInterceptor: job=" + job.getName());
        }
    }
}
