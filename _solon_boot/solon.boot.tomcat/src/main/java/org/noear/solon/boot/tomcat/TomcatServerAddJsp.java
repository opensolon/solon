package org.noear.solon.boot.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2022/8/26 17:37
 **/
public class TomcatServerAddJsp extends TomcatServerBase {
    @Override
    protected Context stepContext() throws Throwable {
        File root = getJspDir();
        Context ctx = _server.addWebapp("/", root.getAbsolutePath());
        scanServlet(ctx);
        return ctx;
    }

    private void scanServlet(Context ctx) {
        //**************session time setting start Session时间相关*****************
        if (ServerProps.session_timeout > 0) {
            ctx.setSessionTimeout(ServerProps.session_timeout);
        }
        //**************session time setting end*****************
        ctx.setAllowCasualMultipartParsing(true);

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        //识别Servlet注解，让JSP与动作分发不冲突xxxDDDD
        File additionWebInfClassesFolder = new File(getServletFolder(), "/org/noear/solon/boot/tomcat");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClassesFolder.getAbsolutePath(), "/"));
        ctx.setResources(resources);
    }


    @Override
    protected Runnable injectedSteps() {
        return () -> System.setProperty("io.message", "Hello JSP!");
    }



    //以XApp为依据的相对路径获取  获取JSP文件路径..
    protected File getJspDir() throws Throwable {
        String dirroot = Utils.getResource("/").toString();
        File dir = new File(URI.create(dirroot));
        if (!dir.exists()) {
            dirroot = dirroot + "src/main/webapp";
            dir = new File(URI.create(dirroot));
        }
        return dir;

    }
}
