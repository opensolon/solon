package org.noear.solon.boot.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.tomcat.http.TCHttpContextHandler;


/**
 * @Created by: Yukai
 * @Date: 2019/3/28 15:49
 * @Description : Yukai is so handsome xxD
 */
public class TomcatServer implements ServerLifecycle {
    private Tomcat tomcat;

    @Override
    public void start(String host, int port) throws Throwable {
        tomcat = new Tomcat();
        tomcat.setBaseDir(System.getProperty("java.io.tmpdir"));
        tomcat.setPort(port);
        if(Utils.isNotEmpty(host)) {
            tomcat.setHostname(host);
        }

        //context configuration start 开始上下文相关配置
        Context context = stepContext();

        //**************session time setting start Session时间相关*****************
        if (ServerProps.session_timeout > 0) {
            context.setSessionTimeout(ServerProps.session_timeout);
        }
        //**************session time setting end*****************
        context.setAllowCasualMultipartParsing(true);

        tomcat.start();
        new Thread(()->{
            tomcat.getServer().await();
        }).start();
    }

    protected Context stepContext() throws Throwable{
        //1.初始化上下文
        Context context = tomcat.addContext("", null);//第二个参数与文档相关

        //2.添加 servlet
        String servletName = "solon";
        tomcat.addServlet(context, servletName, new TCHttpContextHandler());

        //3.建立 servlet 印射
        context.addServletMappingDecoded("/", servletName);//Servlet与对应uri映射

        return context;
    }

    @Override
    public void stop() throws Throwable {
        if (tomcat != null) {
            tomcat.stop();
            tomcat = null;
        }
    }
}
