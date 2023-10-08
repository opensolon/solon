package org.noear.solon.core.util;

/**
 * 线程状态工具
 *
 * @author noear
 * @since 2.5
 */
public class ThreadUtil {
    //
    // 可以进行替换扩展
    //
    private static ThreadUtil global = new ThreadUtil();

    public static ThreadUtil global() {
        return global;
    }

    public static void globalSet(ThreadUtil instance) {
        if (instance != null) {
            global = instance;
        }
    }

    public <T> ThreadLocal<T> newThreadLocal(boolean inheritable) {
        if (inheritable) {
            return new InheritableThreadLocal<>();
        } else {
            return new ThreadLocal<>();
        }
    }
}
