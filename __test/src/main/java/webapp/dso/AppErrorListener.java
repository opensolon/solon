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

import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2020/12/17 created
 */
@Component
public class AppErrorListener implements EventListener<Throwable> {
    @Override
    public void onEvent(Throwable throwable) {
        Context ctx = Context.current();
        if(ctx != null){
            //如果当前有处理上下文，可以拿到请求路径，或者参数什么的...
            //
            ctx.path();
        }
    }
}
