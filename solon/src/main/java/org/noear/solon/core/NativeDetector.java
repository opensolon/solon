package org.noear.solon.core;

/**
 * A common delegate for detecting a GraalVM native image environment.
 *
 * @author Sebastien Deleuze
 * @since 5.3.4
 */
public abstract class NativeDetector {

    // See https://github.com/oracle/graal/blob/master/sdk/src/org.graalvm.nativeimage/src/org/graalvm/nativeimage/ImageInfo.java
    private static final boolean imageCode = (System.getProperty("org.graalvm.nativeimage.imagecode") != null);

    public static final String AOT_PROCESSING = "solon.aot.processing";

    private static final boolean aotRuntime = (System.getProperty(AOT_PROCESSING) != null);

    /**
     * Returns {@code true} if invoked in the context of image building or during image runtime, else {@code false}.
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
