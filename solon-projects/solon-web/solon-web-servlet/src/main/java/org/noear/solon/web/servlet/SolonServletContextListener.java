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
package org.noear.solon.web.servlet;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.server.ServerProps;
import org.noear.solon.core.Constants;
import org.noear.solon.core.event.AppInitEndEvent;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.ContextPathFilter;
import org.noear.solon.core.util.ClassUtil;

import javax.servlet.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Servlet 上下文监听器（一般做为 Servlet 容器应用的基类）
 *
 * @author noear
 * @since 1.10
 */
public class SolonServletContextListener implements ServletContextListener {
    /**
     * 对接 web.xml
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //1.注册初始化事件
        EventBus.subscribe(AppInitEndEvent.class, e -> {
            //禁用内部的http服务，由 war 容器提供
            Solon.app().enableHttp(false);

            //取消文件运行模式
            Solon.cfg().isFilesMode(false);

            //设定 contextPath
            String contextPath = sce.getServletContext().getContextPath();
            if (contextPath.length() > 1) {
                Solon.app().filterIfAbsent(Constants.FT_IDX_CONTEXT_PATH, new ContextPathFilter(contextPath, false));
            }
        });

        //2.注册加载完成事件
        EventBus.subscribe(AppLoadEndEvent.class, e -> {
            ServletRegistration.Dynamic registration = sce.getServletContext().addServlet("solon", SolonServletHandler.class);
            if (registration != null) {
                //mapping
                registration.addMapping("/*");

                //configElement
                int _fileOutputBuffer = 1 * 1024 * 1024;
                long _maxBodySize = (ServerProps.request_maxBodySize > 0 ? ServerProps.request_maxBodySize : -1L);
                long _maxFileSize = (ServerProps.request_maxFileSize > 0 ? ServerProps.request_maxFileSize : -1L);

                MultipartConfigElement configElement = new MultipartConfigElement(
                        System.getProperty("java.io.tmpdir"),
                        _maxFileSize,
                        _maxBodySize,
                        _fileOutputBuffer);
                registration.setMultipartConfig(configElement);
            }

            if(e.app().enableWebSocket()){
                SolonWebSocketEndpoint.addEndpoint(sce.getServletContext());
            }
        });

        //3.执行Main函数
        invokeMain(sce.getServletContext(), new String[0]);
    }

    /**
     * 调用主函数（支持主类配置）
     */
    private void invokeMain(ServletContext sc, String[] strArgs) throws RuntimeException {
        Class<?> mainClass = this.getClass();
        String mainClassStr = sc.getInitParameter("solonMainClass");//v2.5
        if (Utils.isEmpty(mainClassStr)) {
            mainClassStr = sc.getInitParameter("solonStartClass");//@deprecated v2.5
        }

        if (Utils.isNotEmpty(mainClassStr)) {
            mainClass = ClassUtil.loadClass(mainClassStr);

            if (mainClass == null) {
                throw new IllegalStateException("The start class was not found: '" + mainClassStr + "'");
            }
        }

        Method mainMethod = null;
        try {
            mainMethod = mainClass.getMethod("main", String[].class);
        } catch (Exception ex) {
            mainMethod = null;
        }

        if (mainMethod != null && Modifier.isStatic(mainMethod.getModifiers())) {
            try {
                mainMethod.invoke(null, new Object[]{strArgs});
            } catch (Throwable e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new IllegalStateException(e);
                }
            }
        } else {
            throw new IllegalStateException("The main function was not found for: " + this.getClass().getName());
        }
    }

    /**
     * Servlet容器销毁时关闭Solon
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (Solon.cfg().stopSafe()) {
            Solon.stopBlock(false, Solon.cfg().stopDelay());
        } else {
            Solon.stopBlock(false, 0);
        }
    }
}
