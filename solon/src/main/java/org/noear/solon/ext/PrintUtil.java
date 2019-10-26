package org.noear.solon.ext;

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

    public static void println(){
        System.out.println();
    }

    public static void println(String txt){
        System.out.println( txt);
    }

    public static void println(Object obj){
        System.out.println(obj);
    }

    public static void blackln(String txt){
        System.out.println(ANSI_BLACK + txt);
        System.out.print(ANSI_RESET);
    }

    public static void blackln(Object obj){
        System.out.println(ANSI_BLACK + obj);
        System.out.print(ANSI_RESET);
    }

    public static void redln(String txt){
        System.out.println(ANSI_RED + txt);
        System.out.print(ANSI_RESET);
    }

    public static void redln(Object txt){
        System.out.println(ANSI_RED + txt);
        System.out.print(ANSI_RESET);
    }

    public static void blueln(String txt){
        System.out.println(ANSI_BLUE + txt);
        System.out.print(ANSI_RESET);
    }

    public static void blueln(Object txt){
        System.out.println(ANSI_BLUE + txt);
        System.out.print(ANSI_RESET);
    }
}
