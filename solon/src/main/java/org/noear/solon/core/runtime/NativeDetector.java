package org.noear.solon.core.runtime;

/**
 * 用于检测GraalVM本机映像环境的通用委托。
 *
 * @author Sebastien Deleuze
 * @since 2.2
 */
public abstract class NativeDetector {
    public static final String AOT_PROCESSING = "solon.aot.processing";
    public static final String AOT_IMAGECODE = "org.graalvm.nativeimage.imagecode";

    private static final boolean imageCode = (System.getProperty(AOT_IMAGECODE) != null);
    private static final boolean aotRuntime = (System.getProperty(AOT_PROCESSING) != null);

    /**
     * 是否原生镜像上执行
     */
    public static boolean inNativeImage() {
        return imageCode;
    }

    /**
     * 是否在 aot 运行时
     */
    public static boolean isAotRuntime() {
        return aotRuntime;
    }
}
