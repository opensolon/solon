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
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.lang.Nullable;

/**
 * @author noear 2023/1/10 created
 */
@Component(index = 1)
public class RouterInterceptorImpl implements RouterInterceptor {

    @Override
    public void doIntercept(Context ctx, @Nullable Handler handler, RouterInterceptorChain chain) throws Throwable {
        System.out.println("RouterInterceptor: xxxx");
        chain.doIntercept(ctx, handler);
    }

    @Override
    public void postArguments(Context ctx, ParamWrap[] args, Object[] vals) throws Throwable {
        if ("/demo2/param5/test5".equals(ctx.pathNew())) {
            vals[0] = "postArguments";
        }
    }
}
