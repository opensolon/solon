package org.noear.solon.boot.tomcat;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.util.PrintUtil;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.noear.solon.boot.tomcat.XPluginImp.solon_boot_ver;

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

        PrintUtil.info("Server:main: tomcat: Has Stopped " + solon_boot_ver());
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
            connector.setMaxPostSize(ServerProps.request_maxBodySize);
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
