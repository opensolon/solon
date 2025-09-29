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
package org.noear.solon.server.tomcat;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.noear.solon.Utils;
import org.noear.solon.server.ServerLifecycle;

/**
 * @author Yukai
 * @since 2022/8/26 17:01
 **/
public abstract class TomcatServerBase implements ServerLifecycle {
    protected Tomcat _server;

    @Override
    public void start(String host, int port) throws Throwable {
        _server = new Tomcat();

        if (Utils.isNotEmpty(host)) {
            _server.setHostname(host);
        }

        //初始化上下文
        initContext();

        //添加连接端口
        addConnector(port);

        _server.start();
    }


    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.destroy();
            _server = null;
        }
    }

    protected abstract Context initContext() throws Throwable;

    protected abstract void addConnector(int port) throws Throwable;
}
