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
package org.noear.solon.boot.jdkhttp.integration;

import org.noear.solon.Solon;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.jdkhttp.JdkHttpServerComb;
import org.noear.solon.boot.prop.impl.HttpServerProps;
import org.noear.solon.core.*;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;

public final class JdkHttpPlugin implements Plugin {
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    public static String solon_boot_ver() {
        return "jdk http/" + Solon.version();
    }

    JdkHttpServerComb _server;

    @Override
    public void start(AppContext context) {
        if (context.app().enableHttp() == false) {
            return;
        }

        //如果有 jetty 插件，就不启动了
        if (ClassUtil.loadClass("org.noear.solon.boot.jetty.integration.JettyPlugin") != null) {
            return;
        }

        //如果有undrtow插件，就不启动了
        if (ClassUtil.loadClass("org.noear.solon.boot.undertow.integration.UndertowPlugin") != null) {
            return;
        }

        //如果有 vertx 插件，就不启动了
        if (ClassUtil.loadClass("org.noear.solon.boot.vertx.integration.VxHttpPlugin") != null) {
            return;
        }

        //如果有smarthttp插件，就不启动了
        if (ClassUtil.loadClass("org.noear.solon.boot.smarthttp.integration.SmHttpPlugin") != null) {
            return;
        }

        context.lifecycle(ServerConstants.SIGNAL_LIFECYCLE_INDEX, new LifecycleBean() {
            @Override
            public void postStart() throws Throwable {
                start0(context);
            }
        });
    }

    private void start0(AppContext context) throws Throwable {
        //初始化属性
        ServerProps.init();

        HttpServerProps props = new HttpServerProps();
        final String _host = props.getHost();
        final int _port = props.getPort();
        final String _name = props.getName();

        long time_start = System.currentTimeMillis();


        _server = new JdkHttpServerComb();
        _server.setExecutor(props.newWorkExecutor("jdkhttp-"));
        _server.setHandler(context.app()::tryHandle);

        //尝试事件扩展
        EventBus.publish(_server);
        _server.start(_host, _port);


        final String _wrapHost = props.getWrapHost();
        final int _wrapPort = props.getWrapPort();
        _signal = new SignalSim(_name, _wrapHost, _wrapPort, "http", SignalType.HTTP);
        context.app().signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        String httpServerUrl = props.buildHttpServerUrl(_server.isSecure());
        LogUtil.global().info("Connector:main: jdkhttp: Started ServerConnector@{HTTP/1.1,[http/1.1]}{" + httpServerUrl + "}");
        LogUtil.global().info("Server:main: jdkhttp: Started (" + solon_boot_ver() + ") @" + (time_end - time_start) + "ms");
    }


    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.stop();
        _server = null;
        LogUtil.global().info("Server:main: jdkhttp: Has Stopped (" + solon_boot_ver() + ")");
    }
}
