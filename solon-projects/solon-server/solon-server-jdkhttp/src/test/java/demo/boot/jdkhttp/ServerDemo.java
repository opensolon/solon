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
package demo.boot.jdkhttp;

import org.noear.solon.server.jdkhttp.JdkHttpServer;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

import java.util.concurrent.Executors;

/**
 * @author noear 2023/4/6 created
 */
//@Managed
public class ServerDemo implements LifecycleBean , Handler {
    JdkHttpServer _server;

    @Override
    public void start() throws Throwable {
        _server = new JdkHttpServer();
        _server.setExecutor(Executors.newCachedThreadPool());
        _server.setHandler(this); //如果使用 Solon.app()::tryHandle，则转发给 Solon.app()
        _server.start(null, 8801);
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;
        }
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        ctx.output("Hello world!");
    }
}
