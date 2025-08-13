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
import org.noear.solon.server.ServerProps;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2022/8/26 17:01
 **/
public abstract class TomcatServerBase implements ServerLifecycle {
    protected Tomcat _server;

    @Override
    public void start(String host, int port) throws Throwable {
        stepSysProps(injectedSteps());
        stepTomcat(host, port);
        //define context type : jsp/non-jsp
        stepContext();

        _server.start();
    }


    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;
        }
    }



    protected abstract Context stepContext() throws Throwable;

    protected Runnable injectedSteps(){
        return null;
    }



    private void stepSysProps(Runnable injectedSteps) {
        Optional.ofNullable(injectedSteps).ifPresent(Runnable::run);
        System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
    }

    private void stepTomcat(String host, int port) {
        _server = new Tomcat();
        _server.setBaseDir(System.getProperty("java.io.tmpdir"));
        _server.setPort(port);

        if (Utils.isNotEmpty(host)) {
            _server.setHostname(host);
        }

        Connector connector = _server.getConnector();

        if (ServerProps.request_maxBodySize > 0) {
            if (ServerProps.request_maxBodySize > Integer.MAX_VALUE) {
                connector.setMaxPostSize(Integer.MAX_VALUE);
            } else {
                connector.setMaxPostSize((int) ServerProps.request_maxBodySize);
            }
        }
    }


    //算是较为满意的获取被注解的Servlet包下路径
    protected static File getServletFolder() {
        try {
            String runningJarPath = TomcatServerAddJsp
                    .class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI().getPath()
                    .replaceAll("\\\\", "/");
            return new File(runningJarPath);
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}
