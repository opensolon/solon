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

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import org.noear.solon.extend.cron4j.Cron4j;

import java.util.Date;

@Cron4j(cron5x = "*/1 * * * *")
public class Cron4jTask extends Task {

    @Override
    public void execute(TaskExecutionContext context) throws RuntimeException {
        System.out.println("我是定时任务: Cron4jTask(*/1 * * * *) -- " + new Date().toString());
    }
}
