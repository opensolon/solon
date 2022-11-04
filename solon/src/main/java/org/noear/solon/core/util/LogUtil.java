package org.noear.solon.core.util;

/**
 * 日志打印小工具（仅限内部使用）
 *
 * @author noear
 * @since 1.10
 * */
public class LogUtil {
    public static void trace(Object content) {
        System.out.print("[Solon] ");
        PrintUtil.greenln(content);
    }

    public static void debug(Object content) {
        System.out.print("[Solon] ");
        PrintUtil.blueln(content);
    }

    public static void info(Object content) {
        System.out.println("[Solon] " + content);
    }

    public static void warn(Object content) {
        System.out.print("[Solon] ");
        PrintUtil.yellowln(content);
    }

    public static void error(Object content) {
        System.out.print("[Solon] ");
        PrintUtil.redln(content);
    }
}
