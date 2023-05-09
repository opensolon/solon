package org.noear.solon.core.handle;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.wrap.MethodWrap;

import java.lang.reflect.Method;

/**
 * Method Handler（用于处理 Job 之类的简化函数处理）
 *
 * @author noear
 * @since 2.0
 * */
public class MethodHandler implements Handler {
    private final BeanWrap bw;
    private final MethodWrap mw;
    private final boolean allowResult;

    /**
     * @param beanWrap    Bean包装器
     * @param method      函数（外部要控制访问权限）
     * @param allowResult 允许传递结果
     */
    public MethodHandler(BeanWrap beanWrap, Method method, boolean allowResult) {
        this.bw = beanWrap;
        this.mw = beanWrap.context().methodGet(method);
        this.allowResult = allowResult;
    }


    /**
     * 处理
     */
    @Override
    public void handle(Context c) throws Throwable {
        Object tmp = Bridge.actionExecutorDef().execute(c, bw.get(), mw);

        if (allowResult) {
            c.result = tmp;
        }
    }
}