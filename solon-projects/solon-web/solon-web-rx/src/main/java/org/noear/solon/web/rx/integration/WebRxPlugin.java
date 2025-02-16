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
package org.noear.solon.web.rx.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.ParameterizedTypeImpl;
import org.noear.solon.rx.handle.RxChainManager;
import org.noear.solon.rx.handle.RxFilter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author noear
 * @since 2.3
 */
public class WebRxPlugin implements Plugin {

    @Override
    public void start(AppContext context) throws Throwable {
        RxChainManager<Context> chainManager = new RxChainManager<>();
        ParameterizedType filterGenericType = new ParameterizedTypeImpl(RxFilter.class, new Type[]{Context.class});

        context.wrapAndPut("RxChainManager<Context>", chainManager);

        context.subWrapsOfType(RxFilter.class, filterGenericType, bw -> {
            chainManager.addFilter(bw.get(), bw.index());
        });

        context.app().chainManager().addReturnHandler(new ActionReturnRxHandler());
    }
}
