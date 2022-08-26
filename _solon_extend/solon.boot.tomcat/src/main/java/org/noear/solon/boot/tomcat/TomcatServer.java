package org.noear.solon.boot.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.tomcat.http.TCHttpContextHandler;
import org.noear.solon.core.util.NamedThreadFactory;
import org.noear.solon.core.util.PrintUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.noear.solon.boot.tomcat.XPluginImp.solon_boot_ver;


/**
 * @Created by: Yukai
 * @Date: 2019/3/28 15:49
 * @Description : Yukai is so handsome xxD
 */
public class TomcatServer implements ServerLifecycle {
    private Tomcat _server; // 单线程加载机制 无需锁保护
    private static final String TOMCAT_PREFIX = "tomcat-";
    private final ExecutorService _executor = Executors.newSingleThreadExecutor(new NamedThreadFactory(TOMCAT_PREFIX));


    @Override
    public void start(String host, int port) throws Throwable {
        stepTomcat(host, port);
        stepContext();
        startTom();
    }

    private void stepTomcat(String host, int port) {
        _server = new Tomcat();
        _server.setBaseDir(System.getProperty("java.io.tmpdir"));
        _server.setPort(port);
        _server.getConnector();
        if (Utils.isNotEmpty(host)) {
            _server.setHostname(host);
        }
    }

    private Context stepContext() {
        //context configuration start 开始上下文相关配置
        //1.初始化上下文
        Context context = _server.addContext("", null);//第二个参数与文档相关
        //2.添加 servlet
        String servletName = "solon";
        Tomcat.addServlet(context, servletName, new TCHttpContextHandler());
        //3.建立 servlet 印射
        context.addServletMappingDecoded("/", servletName);//Servlet与对应uri映射
        //**************session time setting start Session时间相关*****************
        if (ServerProps.session_timeout > 0) {
            context.setSessionTimeout(ServerProps.session_timeout);
        }
        //**************session time setting end*****************
        context.setAllowCasualMultipartParsing(true);
        return context;
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;
        }

        PrintUtil.info("Server:main: tomcat: Has Stopped " + solon_boot_ver());
    }





    private void startTom() throws LifecycleException, InterruptedException {
        _server.start();
        _executor.execute(() -> {
            _server.getServer().await();
        });
        //给org.apache.catalina.Server.await 点时间
        Thread.sleep(1);
    }
}
