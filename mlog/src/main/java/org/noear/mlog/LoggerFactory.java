package org.noear.mlog;

/**
 * 日志器工厂
 *
 * @author noear
 * @since 1.2
 */
public class LoggerFactory {
    //
    //日志等级
    //
    private static volatile Level level = Level.TRACE;

    public static void setLevel(Level level) {
        LoggerFactory.level = level;
    }

    public static Level getLevel() {
        return LoggerFactory.level;
    }

    //
    //日志器工厂
    //
    private static ILoggerFactory factory = (name) -> new LoggerSimple(name);

    public static ILoggerFactory getFactory() {
        return factory;
    }

    public static void setFactory(ILoggerFactory factory) {
        LoggerFactory.factory = factory;
    }

    //
    //获取
    //
    public static Logger get(String name) {
        return factory.getLogger(name);
    }

    public static Logger get(Class<?> clz) {
        return factory.getLogger(clz);
    }

    static {
        try {
            String implName = "org.noear.mlog.impl.ILoggerFactoryImpl";
            Class<?> implClz = null;

            if (implClz == null) {
                implClz = ClassLoader.getSystemClassLoader().loadClass(implName);
            }

            if (implClz == null) {
                implClz = LoggerFactory.class.getClassLoader().loadClass(implName);
            }

            if (implClz != null) {
                ILoggerFactory implObj = (ILoggerFactory) implClz.newInstance();

                if (implObj != null) {
                    setFactory(implObj);
                }
            }

        } catch (Throwable ex) {
            System.err.println("[warn] org.noear.mlog.ILoggerFactoryImpl load failed");
        }
    }
}
