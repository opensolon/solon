package org.noear.solon.core.util;

/**
 * 日志打印小工具（仅限内部使用）
 *
 * @author noear
 * @since 1.10
 * */
public class LogUtil {
    private static LogUtil global;

    static {
        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        global = ClassUtil.tryInstance("org.noear.solon.extend.impl.LogUtilExt");

        if (global == null) {
            global = new LogUtil();
        }
    }

    public static LogUtil global() {
        return global;
    }

    /**
     * 框架标题
     */
    public static String title() {
        if (LicenceUtil.global().isEnable()) {
            return "[Solon-EE] ";
        } else {
            return "[Solon] ";
        }
    }

    public void trace(String content) {
        System.out.print(title() + " ");

        PrintUtil.purpleln(content);
    }

    /**
     * @deprecated 2.7
     */
    @Deprecated
    public void debugAsync(String content) {
        RunUtil.async(() -> {
            debug(content);
        });
    }

    public void debug(String content) {
        System.out.print(title());
        PrintUtil.blueln(content);
    }

    /**
     * @deprecated 2.7
     */
    @Deprecated
    public void infoAsync(String content) {
        RunUtil.async(() -> {
            info(content);
        });
    }

    public void info(String content) {
        System.out.print(title());
        System.out.println(content);
    }

    public void warn(String content) {
        warn(content, null);
    }

    public void warn(String content, Throwable throwable) {
        System.out.print(title());

        PrintUtil.yellowln("WARN: " + content);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    public void error(String content) {
        error(content, null);
    }

    public void error(String content, Throwable throwable) {
        System.out.print(title());

        PrintUtil.redln("ERROR: " + content);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
}