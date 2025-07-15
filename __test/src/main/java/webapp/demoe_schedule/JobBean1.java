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
package webapp.demoe_schedule;

import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.scheduling.annotation.Scheduled;

/**
 * @author noear 2023/3/20 created
 */
//@Managed
public class JobBean1 {
    @CloudJob
    public void job1(){
        System.out.println("job1");
    }

    @Scheduled(fixedRate = 1000 * 3)
    public void job2(){
        System.out.println("job1");
    }
}
