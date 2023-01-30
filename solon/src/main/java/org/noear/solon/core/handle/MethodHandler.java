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
    private final BeanWrap bWrap;
    private final MethodWrap mWrap;

    /**
     * @param bWrap  Bean包装器
     * @param method 函数（外部要控制访问权限）
     */
    public MethodHandler(BeanWrap bWrap, Method method) {
        this.bWrap = bWrap;
        this.mWrap = bWrap.context().methodGet(method);
    }


    @Override
    public void handle(Context c) throws Throwable {
        c.result = callDo(c, bWrap.get(), mWrap);
    }

    protected Object callDo(Context c, Object obj, MethodWrap mWrap) throws Throwable {
        String ct = c.contentType();

        if (ct != null && mWrap.getParamWraps().length > 0) {
            //
            //仅有参数时，才执行执行其它执行器
            //
            for (ActionExecutor me : Bridge.actionExecutors()) {
                if (me.matched(c, ct)) {
                    return me.execute(c, obj, mWrap);
                }
            }
        }

        return Bridge.actionExecutorDef().execute(c, obj, mWrap);
    }
}
