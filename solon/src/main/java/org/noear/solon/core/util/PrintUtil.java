/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
}
