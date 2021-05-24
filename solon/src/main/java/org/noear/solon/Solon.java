package org.noear.solon;

import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.ext.ConsumerEx;

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
    private static int stopDelay = 10;

    private static SolonApp global;

    /**
     * 全局实例
     */
    public static SolonApp global() {
        return global;
    }

    /**
     * 应用配置
     * */
    public static SolonProps cfg(){
        return global().cfg();
    }


    /**
     * 启动应用（全局只启动一个），执行序列
     *
     * <p>
     * 1.加载配置（约定：application.properties    为应用配置文件）
     * 2.加载自发现插件（约定：/META-INF/solon/*.properties 为插件配置文件）
     * 3.加载注解Bean（约定：@Bean,@Controller,@Interceptor 为bean）
     * 4.执行Bean加载事件（采用：注册事件的方式进行安需通知）
     */
    public static SolonApp start(Class<?> source, String[] args) {
        return start(source, args, null);
    }

    public static SolonApp start(Class<?> source, String[] args, ConsumerEx<SolonApp> initialize) {
        //1.初始化应用，加载配置
        NvMap argx = NvMap.from(args);
        return start(source, argx, initialize);
    }

    public static SolonApp start(Class<?> source, NvMap argx, ConsumerEx<SolonApp> initialize) {
        if (global != null) {
            return global;
        }

        //确定PID
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        String pid = rb.getName().split("@")[0];
        System.setProperty("PID", pid);

        //绑定类加载器
        JarClassLoader.bindingThread();


        PrintUtil.info("App", "Start loading");

        //1.创建应用
        global = new SolonApp(source, argx);

        //2.1.内部初始化（顺序不能乱!）
        global.init();

        //2.2.自定义初始化
        if (initialize != null) {
            try {
                initialize.accept(global);
            } catch (Throwable ex) {
                throw Utils.throwableWrap(ex);
            }
        }

        //3.运行
        global.run();


        //4.安全停止
        stopDelay = Solon.cfg().getInt("solon.stop.delay", 10);

        if (global.enableSafeStop()) {
            //添加关闭勾子
            Runtime.getRuntime().addShutdownHook(new Thread(() -> Solon.stop0(false, stopDelay)));
        } else {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> Solon.stop0(false, 0)));
        }

        //启动完成
        PrintUtil.info("App", "End loading elapsed=" + global.elapsedTimes() + "ms pid=" + pid);

        return global;
    }

    /**
     * 设置安全停止的延时（单位：秒）
     * */
    public static void stopDelaySet(int delay){
        stopDelay = delay;
    }

    public static void stop() {
        stop(stopDelay);
    }
    public static void stop(int delay) {
        new Thread(() -> stop0(true, delay)).start();
    }

    private static void stop0(boolean exit, int delay) {
        if (Solon.global() == null) {
            return;
        }


        if (delay > 0) {

            String hint = "(1.prestop 2.delay 3.stop)";

            PrintUtil.info("App", "Security to stop: begin..." + hint);

            //1.预停止
            Solon.cfg().plugs().forEach(p -> p.prestop());
            PrintUtil.info("App", "Security to stop: 1 completed " + hint);


            //2.延时标停
            int delay1 = (int) (delay * 0.2);
            int delay2 = delay - delay1;

            //一段暂停
            if (delay1 > 0) {
                sleep0(delay1);
            }

            Solon.global().stopped = true;

            //二段暂停
            if (delay2 > 0) {
                sleep0(delay2);
            }

            PrintUtil.info("App", "Security to stop: 2 completed " + hint);

            //3.停止
            Solon.cfg().plugs().forEach(p -> p.stop());
            PrintUtil.info("App", "Security to stop: 3 completed " + hint);
        } else {
            //1.预停止
            Solon.cfg().plugs().forEach(p -> p.prestop());

            //2.标停
            Solon.global().stopped = true;
            //3.停止
            Solon.cfg().plugs().forEach(p -> p.stop());
        }


        //4.直接非正常退出
        if (exit) {
            System.exit(1);
        }
    }

    private static void sleep0(int seconds){
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ex) {

        }
    }
}
