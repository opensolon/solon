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
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.CloudJobInterceptor;
import org.noear.solon.cloud.model.Job;
import org.noear.solon.logging.utils.TagsMDC;

/**
 * @author noear 2022/4/10 created
 */
@Slf4j
public class CloudJobInterceptorImpl implements CloudJobInterceptor {
    @Override
    public void doIntercept(Job job, CloudJobHandler handler) throws Throwable {
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
            CloudClient.metric().addTimer(Solon.cfg().appName(), "job", timespan);
        }
    }
}
