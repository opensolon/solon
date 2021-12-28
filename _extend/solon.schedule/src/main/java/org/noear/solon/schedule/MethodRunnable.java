package org.noear.solon.schedule;

import org.noear.solon.Utils;

import java.lang.reflect.Method;

/**
 * 方法运行器
 *
 * @author noear
 * @since 1.6
 */
public class MethodRunnable implements Runnable {
    private Object target;
    private Method method;

    public MethodRunnable(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    @Override
    public void run() {
        try {
            method.invoke(target);
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            throw new ScheduledException(e);
        }
    }
}
