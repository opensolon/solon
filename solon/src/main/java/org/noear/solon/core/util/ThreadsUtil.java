package org.noear.solon.core.util;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程工具
 *
 * @author noear
 * @since 2.7
 */
public class ThreadsUtil {
    private static Method method_newVirtualThreadPerTaskExecutor;

    public static ExecutorService newVirtualThreadPerTaskExecutor() {
        try {
            if (method_newVirtualThreadPerTaskExecutor == null) {
                method_newVirtualThreadPerTaskExecutor = Executors.class.getDeclaredMethod("newVirtualThreadPerTaskExecutor");
            }

            return (ExecutorService) method_newVirtualThreadPerTaskExecutor.invoke(Executors.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
