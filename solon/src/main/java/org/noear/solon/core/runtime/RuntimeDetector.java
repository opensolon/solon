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
package org.noear.solon.core.runtime;

/**
 * 用于检测GraalVM本机映像环境的通用委托。
 *
 * @author Sebastien Deleuze
 * @since 2.2
 */
public final class RuntimeDetector {
    public static final String AOT_PROCESSING = "solon.aot.processing";
    public static final String AOT_IMAGECODE = "org.graalvm.nativeimage.imagecode";

    private static final boolean imageCode = (System.getProperty(AOT_IMAGECODE) != null);
    private static final boolean aotRuntime = (System.getProperty(AOT_PROCESSING) != null);

    /**
     * 是否在原生镜像上执行
     */
    public static boolean inNativeImage() {
        return imageCode;
    }

    /**
     * 是否不在原生镜像上执行
     */
    public static boolean notInNativeImage() {
        return !imageCode;
    }

    /**
     * 是否在 aot 运行时
     */
    public static boolean isAotRuntime() {
        return aotRuntime;
    }

    /**
     * 是否不在 aot 运行时
     */
    public static boolean isNotAotRuntime() {
        return !aotRuntime;
    }
}