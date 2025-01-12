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
package org.noear.solon.boot.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.tomcat.http.TCHttpContextHandler;


/**
 * @Created by: Yukai
 * @Date: 2019/3/28 15:49
 * @Description : Yukai is so handsome xxD
 */
public class TomcatServer extends TomcatServerBase {

    @Override
    protected Context stepContext() {
        //context configuration start 开始上下文相关配置
        //1.初始化上下文
        Context context = _server.addContext("", null);//第二个参数与文档相关
        //2.添加 servlet
        String servletName = "solon";
        Tomcat.addServlet(context, servletName, new TCHttpContextHandler());
        //3.建立 servlet 映射
        context.addServletMappingDecoded("/", servletName);//Servlet与对应uri映射
        //**************session time setting start Session时间相关*****************
        if (ServerProps.session_timeout > 0) {
            context.setSessionTimeout(ServerProps.session_timeout);
        }
        //**************session time setting end*****************
        context.setAllowCasualMultipartParsing(true);
        return context;
    }

}
