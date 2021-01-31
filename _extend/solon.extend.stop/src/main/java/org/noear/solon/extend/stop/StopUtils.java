package org.noear.solon.extend.stop;

import org.noear.solon.Solon;

/**
 * @author noear
 * @since 1.3
 */
public class StopUtils {
    private static long STOP_DELAY = 10 * 1000;

    public static void stop(boolean exit, long delay) {
        new Thread(() -> stop0(exit, delay)).start();
    }

    protected static void stop0(boolean exit, long delay) {
        if (Solon.global() == null) {
            return;
        }

        //1.预停止
        Solon.cfg().plugs().forEach(p -> p.prestop());
        System.err.println("[solon.stop] prestop completed");

        //2.延时
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {

            }
            System.err.println("[solon.stop] delay completed");
        }

        //3.停目
        Solon.cfg().plugs().forEach(p -> p.stop());

        System.err.println("[solon.stop] stop completed");

        //4.直接退出?
        if (exit) {
            System.exit(0);
        }
    }
}
