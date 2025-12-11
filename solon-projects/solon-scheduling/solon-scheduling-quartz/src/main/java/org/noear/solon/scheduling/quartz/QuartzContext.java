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
package org.noear.solon.scheduling.quartz;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ContextHolder;
import org.quartz.JobExecutionContext;

import java.util.Map;

/**
 * @author noear
 * @since 2.2
 */
public class QuartzContext {
    /**
     * 获取执行上下文
     * */
    public static Context getContext(JobExecutionContext jc){
        Context ctx = Context.current(); //可能是从上层代理已生成, v1.11
        if (ctx == null) {
            ctx = new ContextEmpty();
        }

        //设置请求对象（mvc 时，可以被注入）
        if(ctx instanceof ContextEmpty) {
            ((ContextEmpty) ctx).request(jc);
        }

        for (Map.Entry<String, Object> kv : jc.getJobDetail().getJobDataMap().entrySet()) {
            if (kv.getValue() != null) {
                ctx.paramMap().add(kv.getKey(), kv.getValue().toString());
            }
        }

        return ctx;
    }
}
