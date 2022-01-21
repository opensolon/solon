package org.noear.solon.extend.aspect;

import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.util.ScanUtil;

import java.lang.reflect.InvocationHandler;
import java.util.Comparator;

/**
 * 切面工具
 *
 * @author noear
 * @since 1.5
 * */
public class AspectUtil {
    /**
     * 绑定
     *
     * @param name  注册名字
     * @param typed 注册类型（当 name 不为空时才有效；否则都算 true）
     */
    public static boolean binding(BeanWrap bw, String name, boolean typed) {
        if (bw.proxy() instanceof AspectUtil) {
            return false;
        } else {
            bw.proxySet(BeanProxy.global);
            Aop.context().beanRegister(bw, name, typed);
            return true;
        }
    }

    /**
     * 绑定
     */
    public static boolean binding(BeanWrap bw) {
        return binding(bw, "", false);
    }


    /**
     * 系上拦截代理
     *
     * @since 1.6
     */
    public static <T> T attach(T bean, InvocationHandler handler) {
        return (T) new BeanInvocationHandler(bean, handler).getProxy();
    }

    /**
     * 为搜索的类，系上拦截代理
     */
    public static void attachByScan(String basePackage, InvocationHandler handler) {
        attachByScan(JarClassLoader.global(), basePackage, handler);
    }

    /**
     * 为搜索的类，系上拦截代理
     */
    public static void attachByScan(ClassLoader classLoader, String basePackage, InvocationHandler handler) {
        if (Utils.isEmpty(basePackage)) {
            return;
        }

        if (classLoader == null) {
            return;
        }

        String dir = basePackage.replace('.', '/');

        //扫描类文件并处理（采用两段式加载，可以部分bean先处理；剩下的为第二段处理）
        ScanUtil.scan(classLoader, dir, n -> n.endsWith(".class"))
                .stream()
                .sorted(Comparator.comparing(s -> s.length()))
                .forEach(name -> {
                    String className = name.substring(0, name.length() - 6);

                    Class<?> clz = Utils.loadClass(classLoader, className.replace("/", "."));
                    if (clz != null) {
                        Aop.wrapAndPut(clz).proxySet(new BeanProxy(handler));
                    }
                });
    }
}
