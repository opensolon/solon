package org.noear.solon.boot.tomcat;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.core.JarClassLoader;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;


/**
 * @Created by: Yukai
 * @Date: 2019/3/28 15:49
 * @Description : Yukai is so handsome xxD
 */
public class TomcatServerAddJsp implements ServerLifecycle {
    private static Tomcat tomcat;

    @Override
    public void start(String host,  int port) throws Throwable {
        final String KEY = "io.message";

        tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir(System.getProperty("java.io.tmpdir"));

        File root = getJspDir();
        //  root = new File("");
        System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
        System.setProperty(KEY, "Hello JSP!");

        StandardContext ctx = null;
        try {
            //添加jsp文件识别位置
            ctx = (StandardContext) tomcat.addWebapp("/", root.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
        ctx.setAllowCasualMultipartParsing(true);

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        //识别Servlet注解，让JSP与动作分发不冲突xxxDDDD
        File additionWebInfClassesFolder = new File(getServletFolder(), "/org/noear/solon/boot/tomcat");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClassesFolder.getAbsolutePath(), "/"));
        ctx.setResources(resources);


        tomcat.start();
    }


    //以XApp为依据的相对路径获取  获取JSP文件路径..
    protected File getJspDir() {

        URLClassLoader jspClassLoader = new URLClassLoader(new URL[0], JarClassLoader.global());

        try {

            String dirroot = Utils.getResource("/").toString();
            //System.out.println("jspDir is "+dirroot);
            File dir = new File(URI.create(dirroot));
            //System.out.println("jspFile is "+dir);
            if (!dir.exists()) {
                dirroot = dirroot + "src/main/webapp";
                dir = new File(URI.create(dirroot));
            }
            //InputStream resource_stream = jspClassLoader.getResourceAsStream("");
            //System.out.println("res_stream:"+resource_stream);
            return dir;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //算是较为满意的获取被注解的Servlet包下路径
    protected static File getServletFolder() {
        try {
            String runningJarPath = TomcatServerAddJsp.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceAll("\\\\", "/");
            return new File(runningJarPath);
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (tomcat != null) {
            tomcat.stop();
            tomcat = null;
        }
    }
}
