package org.noear.solon.extend.hotdev;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

public class Debugger {
    private static URLClassLoader baseLoader;
    private static String[] _args;
    private static String _source;
    private static Class helper;

    static {
        baseLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
    }

    public static void start(Class source, String[] args) {
        _args = args;
        _source = source.getName();
        start0();
        new ChangeWatcher(null, () -> restart()).start();
    }

    private static void start0() {
        ClassLoader classLoader = new URLClassLoader(baseLoader.getURLs(), baseLoader.getParent());
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            helper = classLoader.loadClass(SolonHelper.class.getName());
            Method method = helper.getDeclaredMethod("start", String.class, String[].class);
            method.invoke(helper, _source, _args);

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void stop() {
        if (helper == null) {
            return;
        }

        try {
            Method stop = helper.getDeclaredMethod("stop");
            stop.invoke(helper);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void restart() {
        stop();
        start0();
    }
}
