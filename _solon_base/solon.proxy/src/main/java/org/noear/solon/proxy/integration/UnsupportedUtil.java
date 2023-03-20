package org.noear.solon.proxy.integration;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.LoadBalance;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.wrap.ClassWrap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 未支持检测工具
 *
 * @author noear
 * @since  2.2
 */
public class UnsupportedUtil {

    /**
     * 检测不支持情况（日志提示）
     * */
    public static void check(Class<?> clz, AopContext context, Annotation anno) {
        beanShapeCheck(clz, "@" + anno.annotationType().getSimpleName());
        beanExtractCheck(clz, context);
    }

    private static void beahShapeHint(String target, String annoName, Class<?> clz) {
        LogUtil.global().warn("'" + target + "' not support " + annoName + " annotations, please use @Component: " + clz.getName());
    }


    private static void beanExtractHint(String target, Class<?> clz) {
        LogUtil.global().warn("The '@" + target + "' function supports only class @Component annotations: " + clz.getName());
    }

    private static void beanShapeCheck(Class<?> clz, String annoName) {
        //LifecycleBean（替代 Plugin，提供组件的生态周期控制）
        if (LifecycleBean.class.isAssignableFrom(clz)) {
            beahShapeHint("LifecycleBean", annoName, clz);
        }

        //EventListener
        if (EventListener.class.isAssignableFrom(clz)) {
            beahShapeHint("EventListener", annoName, clz);
        }

        //LoadBalance.Factory
        if (LoadBalance.Factory.class.isAssignableFrom(clz)) {
            beahShapeHint("LoadBalance.Factory", annoName, clz);
        }

        //Handler
        if (Handler.class.isAssignableFrom(clz)) {
            beahShapeHint("Handler", annoName, clz);
        }

        //Filter
        if (Filter.class.isAssignableFrom(clz)) {
            beahShapeHint("Filter", annoName, clz);
        }

        //RouterInterceptor
        if (RouterInterceptor.class.isAssignableFrom(clz)) {
            beahShapeHint("RouterInterceptor", annoName, clz);
        }
    }

    private static void beanExtractCheck(Class<?> clz, AopContext context) {
        ClassWrap clzWrap = ClassWrap.get(clz);

        for (Method m : clzWrap.getMethods()) {
            for (Annotation a : m.getAnnotations()) {
                if (context.beanExtractorHas(a.annotationType())) {
                    beanExtractHint(a.annotationType().getSimpleName(), clz);
                }
            }
        }
    }
}
