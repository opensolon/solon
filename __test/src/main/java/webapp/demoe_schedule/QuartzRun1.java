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

import org.noear.solon.scheduling.annotation.Scheduled;

import java.util.Date;

@Scheduled(fixedRate = 200  ,name = "QuartzRun1")
public class QuartzRun1 implements Runnable {
    @Override
    public void run() {
        System.out.println("我是定时任务: QuartzRun1(200ms) -- " + new Date().toString());
        //throw new RuntimeException("异常");
        System.out.println("如果间隔太长，我可能被配置给控制了");
    }
}
