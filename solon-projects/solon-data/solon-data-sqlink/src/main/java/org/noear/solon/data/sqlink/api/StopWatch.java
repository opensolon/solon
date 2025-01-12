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
package org.noear.solon.data.sqlink.api;

/**
 * @author kiryu1223
 * @since 3.0
 */
class StopWatch {
    public enum Style {
        NANO,
        MILLISECOND
    }

    public static void setStyle(Style style) {
        StopWatch.style = style;
    }

    private static Style style = Style.NANO;
    private static long click;

    public static void start() {
        switch (style) {
            case NANO:
                click = System.nanoTime();
                break;
            case MILLISECOND:
                click = System.currentTimeMillis();
                break;
        }
    }

    public static void end(String make) {
        switch (style) {
            case NANO:
                System.out.printf(make + "耗时%d纳秒%n", System.nanoTime() - click);
                break;
            case MILLISECOND:
                System.out.printf(make + "耗时%d毫秒%n", System.currentTimeMillis() - click);
                break;
        }
    }

    public static void end() {
        switch (style) {
            case NANO:
                System.out.println(System.nanoTime() - click);
                break;
            case MILLISECOND:
                System.out.println(System.currentTimeMillis() - click);
                break;
        }
    }
}
