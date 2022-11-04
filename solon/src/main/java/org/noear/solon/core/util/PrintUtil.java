package org.noear.solon.core.util;

/**
 * 彩色打印小工具（仅限内部使用）
 *
 * @author noear
 * @since 1.0
 * */
public class PrintUtil {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    public static void blackln(Object txt) {
        colorln(ANSI_BLACK, txt);
    }

    public static void redln(Object txt) {
        colorln(ANSI_RED, txt);
    }

    public static void blueln(Object txt) {
        colorln(ANSI_BLUE, txt);
    }

    public static void greenln(Object txt) {
        colorln(ANSI_GREEN, txt);
    }

    public static void purpleln(Object txt) {
        colorln(ANSI_PURPLE, txt);
    }

    public static void yellowln(Object txt) {
        colorln(ANSI_YELLOW, txt);
    }

    public static void colorln(String color, Object s) {
        if (JavaUtil.IS_WINDOWS) {
            System.out.println(s);
        } else {
            System.out.println(color + s);
            System.out.print(ANSI_RESET);
        }
    }

    ////////////////////////

    /**
     * @deprecated 1.10
     * */
    @Deprecated
    public static void debug(Object content) {
        System.out.print("[Solon] ");
        PrintUtil.blueln(content);
    }

    /**
     * @deprecated 1.10
     * */
    @Deprecated
    public static void debug(String label, Object content) {
        System.out.print("[Solon] ");
        PrintUtil.blueln(label + ": " + content);
    }

    /**
     * @deprecated 1.10
     * */
    @Deprecated
    public static void info(Object content) {
        System.out.println("[Solon] " + content);
    }

    /**
     * @deprecated 1.10
     * */
    @Deprecated
    public static void info(String label, Object content) {
        System.out.print("[Solon] ");
        PrintUtil.greenln(label + ": " + content);
    }

    /**
     * @deprecated 1.10
     * */
    @Deprecated
    public static void warn(Object content) {
        System.out.print("[Solon] ");
        PrintUtil.yellowln(content);
    }

    /**
     * @deprecated 1.10
     * */
    @Deprecated
    public static void warn(String label, Object content) {
        System.out.print("[Solon] ");
        PrintUtil.yellowln(label + ": " + content);
    }

    /**
     * @deprecated 1.10
     * */
    @Deprecated
    public static void error(Object content) {
        System.out.print("[Solon] ");
        PrintUtil.redln(content);
    }

    /**
     * @deprecated 1.10
     * */
    @Deprecated
    public static void error(String label, Object content) {
        System.out.print("[Solon] ");
        PrintUtil.redln(label + ": " + content);
    }
}
