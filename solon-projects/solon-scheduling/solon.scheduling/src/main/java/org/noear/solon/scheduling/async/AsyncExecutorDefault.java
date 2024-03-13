package org.noear.solon.scheduling.async;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.scheduling.annotation.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * 异步执行器默认实现
 *
 * @author noear
 * @since 2.4
 */
public class AsyncExecutorDefault implements AsyncExecutor {

    private static final Logger logger = LoggerFactory.getLogger(AsyncExecutorDefault.class);
    @Override
    public Future submit(Invocation inv, Async anno) throws Throwable{
        Class<?> returnType = inv.method().getReturnType();
        if(!"void".equals(returnType.getTypeName())){
            logger.warn("调用了带有返回值的异步方法");
        }
        if (returnType.isAssignableFrom(Future.class)) {
            return (Future) inv.invoke();
        } else {
            return RunUtil.async(() -> {
                try {
                    return inv.invoke();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
