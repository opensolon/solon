package org.noear.solon.web.servlet;

import org.noear.solon.Solon;
import org.noear.solon.core.event.AppInitEndEvent;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.ContextPathFilter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Servlet 上下文监听器（一般做为 Servlet 容器应用的基类）
 *
 * @author noear
 * @since 1.10
 */
public abstract class SolonServletContextListener implements ServletContextListener {
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
                Solon.app().filter(new ContextPathFilter(contextPath + "/"));
            }
        });

        //2.注册加载完成事件
        EventBus.subscribe(AppLoadEndEvent.class, e -> {
            ServletRegistration registration = sce.getServletContext().addServlet("solon", SolonServletHandler.class);
            registration.addMapping("/*");
        });

        //3.执行Main函数
        Method mainMethod = getMain();
        if (mainMethod != null && Modifier.isStatic(mainMethod.getModifiers())) {
            try {
                mainMethod.invoke(null, new Object[]{new String[0]});
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

    private Method getMain() {
        try {
            return this.getClass().getMethod("main", String[].class);
        } catch (Exception ex) {
            return null;
        }
    }
}
