package org.noear.solon;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.ConsumerEx;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * 应用管理中心
 *
 * <pre><code>
 * public class DemoApp{
 *     public static void main(String[] args){
 *         Solon.start(DemoApp.class, args);
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
public class Solon {
    //默认停止延时
    private static int stopDelay = 10;
    //全局实例
    private static SolonApp app;
    //全局默认编码
    private static String encoding = "utf-8";

    /**
     * @deprecated 1.8
     * */
    @Deprecated
    public static SolonApp global() {
        return app;
    }

    /**
     * 全局实例
     */
    public static SolonApp app() {
        return app;
    }

    /**
     * 应用上下文
     */
    public static AopContext context() {
        if (app == null) {
            return null;
        } else {
            return app.context();
        }
    }

    /**
     * 应用配置
     */
    public static SolonProps cfg() {
        return app().cfg();
    }

    /**
     * 全局默认编码
     */
    public static String encoding() {
        return encoding;
    }

    /**
     * 全局默认编码设置
     */
    public static void encodingSet(String charset) {
        //只能在初始化之前设置
        if (app == null && Utils.isNotEmpty(charset)) {
            encoding = charset;
        }
    }

    public static void startIsolatedApp(SolonApp isolatedApp) throws Throwable {
        if (app == null) {
            app = isolatedApp;
        }

        isolatedApp.start(null);
    }

    /**
     * 启动应用（全局只启动一个）
     *
     * @param source 主应用包（用于定制Bean所在包）
     * @param args   启动参数
     */
    public static SolonApp start(Class<?> source, String[] args) {
        return start(source, args, null);
    }

    /**
     * 启动应用（全局只启动一个）
     *
     * @param source     主应用包（用于定制Bean所在包）
     * @param args       启动参数
     * @param initialize 实始化函数
     */
    public static SolonApp start(Class<?> source, String[] args, ConsumerEx<SolonApp> initialize) {
        //1.初始化应用，加载配置
        NvMap argx = NvMap.from(args);
        return start(source, argx, initialize);
    }

    /**
     * 启动应用（全局只启动一个）
     *
     * @param source     主应用包（用于定制Bean所在包）
     * @param argx       启动参数
     * @param initialize 实始化函数
     */
    public static SolonApp start(Class<?> source, NvMap argx, ConsumerEx<SolonApp> initialize) {
        if (app != null) {
            return app;
        }

        //设置文件编码
        if (Utils.isNotEmpty(encoding)) {
            System.setProperty("file.encoding", encoding);
        }

        //设置 headless 默认值
        System.getProperties().putIfAbsent("java.awt.headless", "true");

        //确定PID
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        String pid = rb.getName().split("@")[0];
        System.setProperty("PID", pid);

        //绑定类加载器（即替换当前线程[即主线程]的类加载器）
        JarClassLoader.bindingThread();

        LogUtil.global().info("App: Start loading");

        try {
            //1.创建全局应用及配置
            app = new SolonApp(source, argx);
            app.start(initialize);

        } catch (Throwable e) {
            //显示异常信息
            e = Utils.throwableUnwrap(e);
            EventBus.push(e);

            if (app.enableErrorAutoprint() == false) {
                e.printStackTrace();
            }

            //4.停止服务并退出（主要是停止插件）
            Solon.stop0(true, 0);
            return null;
        }


        //5.初始化安全停止
        stopDelay = Solon.cfg().getInt("solon.stop.delay", 10);

        if (app.enableSafeStop()) {
            //添加关闭勾子
            Runtime.getRuntime().addShutdownHook(new Thread(() -> Solon.stop0(false, stopDelay)));
        } else {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> Solon.stop0(false, 0)));
        }

        //启动完成
        LogUtil.global().info("App: End loading elapsed=" + app.elapsedTimes() + "ms pid=" + pid + " v=" + app.cfg().version());

        return app;
    }

    /**
     * 设置停止延时时间（单位：秒）
     *
     * @param delay 延迟时间（单位：秒）
     */
    public static void stopDelaySet(int delay) {
        stopDelay = delay;
    }

    /**
     * 停止应用
     */
    public static void stop() {
        stop(stopDelay);
    }

    /**
     * 停止应用
     *
     * @param delay 延迟时间（单位：秒）
     */
    public static void stop(int delay) {
        new Thread(() -> stop0(true, delay)).start();
    }

    private static void stop0(boolean exit, int delay) {
        if (Solon.app() == null) {
            return;
        }


        if (delay > 0) {

            String hint = "(1.prestop 2.delay 3.stop)";

            LogUtil.global().info("App: Security to stop: begin..." + hint);

            //1.预停止
            Solon.cfg().plugs().forEach(p -> p.prestop());
            LogUtil.global().info("App: Security to stop: 1 completed " + hint);


            //2.延时标停
            int delay1 = (int) (delay * 0.2);
            int delay2 = delay - delay1;

            //一段暂停
            if (delay1 > 0) {
                sleep0(delay1);
            }

            Solon.app().stopped = true;

            //二段暂停
            if (delay2 > 0) {
                sleep0(delay2);
            }

            LogUtil.global().info("App: Security to stop: 2 completed " + hint);

            //3.停止
            Solon.cfg().plugs().forEach(p -> p.stop());
            LogUtil.global().info("App: Security to stop: 3 completed " + hint);
        } else {
            //1.预停止
            Solon.cfg().plugs().forEach(p -> p.prestop());

            //2.标停
            Solon.app().stopped = true;
            //3.停止
            Solon.cfg().plugs().forEach(p -> p.stop());
        }


        //4.直接非正常退出
        if (exit) {
            System.exit(1);
        }
    }

    private static void sleep0(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ex) {

        }
    }
}
