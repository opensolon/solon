package org.noear.solon.extend.hotdev;


import org.noear.solon.SolonApp;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.ext.ConsumerEx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 热开发模式
 *
 * @author 夜の孤城
 * @since 1.5
 * */
public class Hotdev {
    private static JarClassLoader baseLoader;
    private static Class hotdevProxy;

    private static String[] _args;
    private static String _source;
    private static ConsumerEx<SolonApp> _initialize;


    static {
        baseLoader = JarClassLoader.global();
    }

    /**
     * 启动
     */
    public static void start(Class source, String[] args) {
        start(source, args, null);
    }

    /**
     * 启动
     */
    public static void start(Class source, String[] args, ConsumerEx<SolonApp> initialize) {
        _initialize = initialize;
        _args = args;
        _source = source.getName();

        start0();

        new HotdevWatcher(null, () -> restart()).start();
    }

    private static void start0() {
        JarClassLoader classLoader = new JarClassLoader(baseLoader.getURLs(), baseLoader.getParent());
        Thread.currentThread().setContextClassLoader(classLoader);
        JarClassLoader.globalSet(classLoader);

        try {
            hotdevProxy = classLoader.loadClass(HotdevProxy.class.getName());

            Method method = hotdevProxy.getDeclaredMethod("start", String.class, String[].class);
            method.invoke(hotdevProxy, _source, _args, _initialize);

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止
     */
    private static void stop() {
        if (hotdevProxy == null) {
            return;
        }

        try {
            Method stop = hotdevProxy.getDeclaredMethod("stop");
            stop.invoke(hotdevProxy);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重启
     */
    public static void restart() {
        stop();
        start0();
    }
}
