package org.noear.solon.scheduling.simple.integration;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.scheduling.ScheduledException;


/**
 * 类运行器（支持非单例）
 *
 * @author noear
 * @since 2.2
 */
public class BeanRunnable implements Runnable {
    private BeanWrap target;

    public BeanRunnable(BeanWrap target) {
        this.target = target;
    }

    @Override
    public void run() {
        try {
            ((Runnable) target.get()).run();
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            throw new ScheduledException(e);
        }
    }
}
