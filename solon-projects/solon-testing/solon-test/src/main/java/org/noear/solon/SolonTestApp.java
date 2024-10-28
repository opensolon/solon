/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon;

import org.noear.solon.core.NvMap;
import org.noear.solon.core.util.ConsumerEx;

/**
 * @author noear
 * @since 1.12
 */
public class SolonTestApp extends SolonApp {
    public SolonTestApp(Class<?> source, NvMap args) throws Exception {
        super(source, args.set("testing", "1"));
        Solon.appSet(this);
    }

    @Override
    public void start(ConsumerEx<SolonApp> initialize) throws Throwable {
        //默认关闭 http（避免与已经存在的服务端口冲突）
        enableHttp(false);
        super.start(initialize);
    }
}
