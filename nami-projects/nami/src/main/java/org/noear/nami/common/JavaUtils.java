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
package org.noear.nami.common;

import java.io.File;

/**
 * Java 工具类（获取版本信息）
 *
 * @author noear
 * @since 1.10
 */
public class JavaUtils {
    /**
     * Java 版本号
     * */
    public static final int JAVA_MAJOR_VERSION;

    /**
     * 是否为 Windows
     */
    public static final boolean IS_WINDOWS = (File.separatorChar == '\\');

    /*
     * 获取 Java 版本号
     * http://openjdk.java.net/jeps/223
     * 1.8.x  = 8
     * 11.x   = 11
     * 17.x   = 17
     */
    static {
        int majorVersion;
        try {
            String version = System.getProperty("java.specification.version");
            if (version.startsWith("1.")) {
                version = version.substring(2);
            }
            majorVersion = Integer.parseInt(version);
        } catch (Throwable ignored) {
            majorVersion = 8;
        }
        JAVA_MAJOR_VERSION = majorVersion;
    }
}
