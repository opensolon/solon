package org.noear.solon.proxy;

import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.ScanUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 代理工具
 *
 * @author noear
 * @since 1.5
 * @since 2.1
 * */
public class ProxyUtil {
    /**
     * 绑定代理
     *
     * @param name  注册名字
     * @param typed 注册类型（当 name 不为空时才有效；否则都算 true）
     */
    public static boolean binding(BeanWrap bw, String name, boolean typed) {
        if (bw.proxy() instanceof ProxyUtil) {
            return false;
        } else {
            if (bw.clz().isInterface()) {
                throw new IllegalStateException("Interfaces are not supported as proxy components: " + bw.clz().getName());
            }

            int modifier = bw.clz().getModifiers();
            if (Modifier.isFinal(modifier)) {
                throw new IllegalStateException("Final classes are not supported as proxy components: " + bw.clz().getName());
            }

            if (Modifier.isAbstract(modifier)) {
                throw new IllegalStateException("Abstract classes are not supported as proxy components: " + bw.clz().getName());
            }

            if (Modifier.isPublic(modifier) == false) {
                throw new IllegalStateException("Not public classes are not supported as proxy components: " + bw.clz().getName());
            }

            bw.proxySet(BeanProxy.getGlobal());
            bw.context().beanRegister(bw, name, typed);
            bw.context().beanShapeRegister(bw.clz(), bw, bw.clz());

            return true;
        }
    }

    /**
     * 绑定代理
     */
    public static boolean binding(BeanWrap bw) {
        return binding(bw, "", false);
    }


    /////////////////////////////////////////////

    private static Set<Class<?>> tryAttachCached = new HashSet<>();

    /**
     * 为类，系上拦截代理
     *
     * @since 1.6
     */
    public static void attach(AopContext aopContext, Class<?> clz, InvocationHandler handler) {
        attach(aopContext, clz, null, handler);
    }

    public static void attach(AopContext aopContext, Class<?> clz,  Object obj, InvocationHandler handler) {
        if (clz.isAnnotation() || clz.isInterface() || clz.isEnum() || clz.isPrimitive()) {
            return;
        }

        //去重处理
        if (tryAttachCached.contains(clz)) {
            return;
        } else {
            tryAttachCached.add(clz);
        }

        aopContext.wrapAndPut(clz, obj).proxySet(new BeanProxy(handler));
    }

    /**
     * 为搜索的类，系上拦截代理
     *
     * @param basePackage 基础包名
     * @param handler     拦截代理
     */
    public static void attachByScan(AopContext aopContext, String basePackage, InvocationHandler handler) {
        attachByScan(aopContext, basePackage, null, handler);
    }


    /**
     * 为搜索的类，系上拦截代理
     *
     * @param aopContext  类加载器
     * @param basePackage 基础包名
     * @param filter      过滤器
     * @param handler     拦截代理
     */
    public static void attachByScan(AopContext aopContext, String basePackage, Predicate<String> filter, InvocationHandler handler) {
        if (Utils.isEmpty(basePackage)) {
            return;
        }

        if (aopContext == null) {
            return;
        }

        if (filter == null) {
            filter = (s) -> true;
        }

        String dir = basePackage.replace('.', '/');

        //扫描类文件并处理（采用两段式加载，可以部分bean先处理；剩下的为第二段处理）
        ScanUtil.scan(aopContext.getClassLoader(), dir, n -> n.endsWith(".class"))
                .stream()
                .sorted(Comparator.comparing(s -> s.length()))
                .filter(filter)
                .forEach(name -> {
                    String className = name.substring(0, name.length() - 6);

                    Class<?> clz = ClassUtil.loadClass(aopContext.getClassLoader(), className.replace("/", "."));
                    if (clz != null) {
                        attach(aopContext, clz, handler);
                    }
                });
    }
}
