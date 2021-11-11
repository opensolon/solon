package org.noear.solon.extend.hotdev;


import org.noear.solon.core.JarClassLoader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * 热开发模式
 *
 * @author 夜の孤城
 * @since 1.5
 * */
public class Hotdev {
    private static ClassLoader parentLoader;
    private static Class hotdevProxy;

    private static String[] _args;
    private static String _source;
    private static URL[] classPaths;

    static {
        initPaths();
    }

    /**
     * 初始化加载路径
     */
    private static void initPaths() {
        parentLoader = Hotdev.class.getClassLoader().getParent();

        //从classpath中获取加载路径
        String[] classPathArray = System.getProperty("java.class.path").split(File.pathSeparator);
        classPaths = new URL[classPathArray.length];

        for (int i = 0; i < classPathArray.length; i++) {
            try {
                classPaths[i] = new File(classPathArray[i]).toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 启动
     */
    public static void start(Class source, String[] args) {
        _args = args;
        _source = source.getName();
        start0();

        new HotdevWatcher(null, () -> restart()).start();
    }

    private static void start0() {
        //新建一个和当前加类加载器同级的加载，用于隔离类资源。
        // 同时把当前加载器的urls给它，让它可以找到所有的资源
        JarClassLoader classLoader = new JarClassLoader(classPaths, parentLoader);
        //切换环境
        Thread.currentThread().setContextClassLoader(classLoader);

        try {
            hotdevProxy = classLoader.loadClass(HotdevProxy.class.getName());
            Method method = hotdevProxy.getDeclaredMethod("start", String.class, String[].class);
            method.invoke(hotdevProxy, _source, _args);

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
