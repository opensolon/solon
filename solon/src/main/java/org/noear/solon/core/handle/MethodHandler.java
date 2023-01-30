package org.noear.solon.core.handle;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.wrap.MethodWrap;

import java.lang.reflect.Method;

/**
 * Method Handler
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

    public MethodWrap method() {
        return mw;
    }


    /**
     * 处理
     * */
    @Override
    public void handle(Context c) throws Throwable {
        Object tmp = execute(c, bw.get());

        if (allowResult) {
            c.result = tmp;
        }
    }

    /**
     * 执行
     */
    public Object execute(Context c, Object obj) throws Throwable {
        String ct = c.contentType();

        if (ct != null && mw.getParamWraps().length > 0) {
            //
            //仅有参数时，才执行执行其它执行器
            //
            for (ActionExecutor me : Bridge.actionExecutors()) {
                if (me.matched(c, ct)) {
                    return me.execute(c, obj, mw);
                }
            }
        }

        return Bridge.actionExecutorDef().execute(c, obj, mw);
    }
}