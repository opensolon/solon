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
package org.noear.solon.boot.jlhttp.test;

import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.boot.jlhttp.JlHttpServer;
import org.noear.solon.core.bean.LifecycleBean;

import java.util.concurrent.Executors;

/**
 * @author noear 2023/4/6 created
 */
@SolonMain
public class ServerDemo {

    public static void main(String[] args) {
        Solon.start(ServerDemo.class, args, app -> {
            if (app.cfg().argx().containsKey("ssl")) {
                app.context().lifecycle(new ServerImpl());
            }
        });
    }

    public static class ServerImpl implements LifecycleBean {
        JlHttpServer _server;

        @Override
        public void start() throws Throwable {
            _server = new JlHttpServer();
            _server.setExecutor(Executors.newCachedThreadPool());
            _server.setHandler(Solon.app()::tryHandle); //如果使用 Solon.app()::tryHandle，则转发给 Solon.app()
            _server.enableSsl(false, null);
            _server.start(null, Solon.cfg().serverPort() + 1);
        }

        @Override
        public void stop() throws Throwable {
            if (_server != null) {
                _server.stop();
                _server = null;
            }
        }
    }
}
