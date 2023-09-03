package org.noear.solon.proxy;

import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
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

            //添加bean形态处理
            bw.context().beanShapeRegister(bw.clz(), bw, bw.clz());

            //注册到容器
            bw.context().beanRegister(bw, name, typed);

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
     * @param appContext 应用上下文
     * @param targetClz  目标类
     * @param handler    调用处理
     * @since 1.6
     */
    public static void attach(AppContext appContext, Class<?> targetClz, InvocationHandler handler) {
        attach(appContext, targetClz, null, handler);
    }

    /**
     * 为类，系上拦截代理
     *
     * @param appContext 应用上下文
     * @param targetClz  目标类
     * @param targetObj  目标对象
     * @param handler    调用处理
     * @since 1.6
     */
    public static void attach(AppContext appContext, Class<?> targetClz, Object targetObj, InvocationHandler handler) {
        if (targetClz.isAnnotation() || targetClz.isInterface() || targetClz.isEnum() || targetClz.isPrimitive()) {
            return;
        }

        //去重处理
        if (tryAttachCached.contains(targetClz)) {
            return;
        } else {
            tryAttachCached.add(targetClz);
        }

        appContext.wrapAndPut(targetClz, targetObj).proxySet(new BeanProxy(handler));
    }

    /**
     * 为搜索的类，系上拦截代理
     *
     * @param appContext  应用上下文
     * @param basePackage 基础包名
     * @param handler     拦截代理
     */
    public static void attachByScan(AppContext appContext, String basePackage, InvocationHandler handler) {
        attachByScan(appContext, basePackage, null, handler);
    }


    /**
     * 为搜索的类，系上拦截代理
     *
     * @param appContext  应用上下文
     * @param basePackage 基础包名
     * @param filter      过滤器
     * @param handler     拦截代理
     */
    public static void attachByScan(AppContext appContext, String basePackage, Predicate<String> filter, InvocationHandler handler) {
        if (Utils.isEmpty(basePackage)) {
            return;
        }

        if (appContext == null) {
            return;
        }

        if (filter == null) {
            filter = (s) -> true;
        }

        String dir = basePackage.replace('.', '/');

        //扫描类文件并处理（采用两段式加载，可以部分bean先处理；剩下的为第二段处理）
        ScanUtil.scan(appContext.getClassLoader(), dir, n -> n.endsWith(".class"))
                .stream()
                .sorted(Comparator.comparing(s -> s.length()))
                .filter(filter)
                .forEach(name -> {
                    String className = name.substring(0, name.length() - 6);

                    Class<?> clz = ClassUtil.loadClass(appContext.getClassLoader(), className.replace("/", "."));
                    if (clz != null) {
                        attach(appContext, clz, handler);
                    }
                });
    }
}
