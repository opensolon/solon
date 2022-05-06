package org.noear.solon.core.util;

import java.io.File;

/**
 * 彩色打印小工具
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

    /**
     * 是否为 Windows
     */
    public static final boolean IS_WINDOWS = (File.separatorChar == '\\');


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
        if (IS_WINDOWS) {
            System.out.println(s);
        } else {
            System.out.println(color + s);
            System.out.print(ANSI_RESET);
        }
    }


    public static void debug(Object content) {
        System.out.print("[Solon] ");
        blueln(content);
    }

    public static void debug(String label, Object content) {
        System.out.print("[Solon] ");
        blueln(label + ": " + content);
    }

    public static void info(Object content) {
        System.out.println("[Solon] " + content);
    }

    public static void info(String label, Object content) {
        System.out.print("[Solon] ");
        greenln(label + ": " + content);
    }

    public static void warn(Object content) {
        System.out.print("[Solon] ");
        yellowln(content);
    }

    public static void warn(String label, Object content) {
        System.out.print("[Solon] ");
        yellowln(label + ": " + content);
    }

    public static void error(Object content) {
        System.out.print("[Solon] ");
        redln(content);
    }

    public static void error(String label, Object content) {
        System.out.print("[Solon] ");
        redln(label + ": " + content);
    }
}
