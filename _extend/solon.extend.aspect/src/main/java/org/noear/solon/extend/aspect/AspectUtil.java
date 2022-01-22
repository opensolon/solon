package org.noear.solon.extend.aspect;

import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.util.ScanUtil;

import java.lang.reflect.InvocationHandler;
import java.util.Comparator;
import java.util.function.Predicate;

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
     * 为类，系上拦截代理
     *
     * @since 1.6
     */
    public static void attach(Class<?> clz, InvocationHandler handler) {
        if (clz.isAnnotation() || clz.isInterface() || clz.isEnum() || clz.isPrimitive()) {
            return;
        }

        Aop.wrapAndPut(clz).proxySet(new BeanProxy(handler));
    }

    /**
     * 为搜索的类，系上拦截代理
     *
     * @param basePackage 基础包名
     * @param handler     拦截代理
     */
    public static void attachByScan(String basePackage, InvocationHandler handler) {
        attachByScan(JarClassLoader.global(), basePackage, null, handler);
    }

    /**
     * 为搜索的类，系上拦截代理
     *
     * @param basePackage 基础包名
     * @param filter      过滤器
     * @param handler     拦截代理
     */
    public static void attachByScan(String basePackage, Predicate<String> filter, InvocationHandler handler) {
        attachByScan(JarClassLoader.global(), basePackage, filter, handler);
    }

    /**
     * 为搜索的类，系上拦截代理
     *
     * @param classLoader 类加载器
     * @param basePackage 基础包名
     * @param filter      过滤器
     * @param handler     拦截代理
     */
    public static void attachByScan(ClassLoader classLoader, String basePackage, Predicate<String> filter, InvocationHandler handler) {
        if (Utils.isEmpty(basePackage)) {
            return;
        }

        if (classLoader == null) {
            return;
        }

        if (filter == null) {
            filter = (s) -> true;
        }

        String dir = basePackage.replace('.', '/');

        //扫描类文件并处理（采用两段式加载，可以部分bean先处理；剩下的为第二段处理）
        ScanUtil.scan(classLoader, dir, n -> n.endsWith(".class"))
                .stream()
                .sorted(Comparator.comparing(s -> s.length()))
                .filter(filter)
                .forEach(name -> {
                    String className = name.substring(0, name.length() - 6);

                    Class<?> clz = Utils.loadClass(classLoader, className.replace("/", "."));
                    if (clz != null) {
                        attach(clz, handler);
                    }
                });
    }
}
