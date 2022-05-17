package org.noear.solon.plugind;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;

/**
 * 外接小程序（由独立 ClassLoader 加载的，可动态管理的 Plugin）
 *
 * @author noear
 * @since 1.7
 */
public abstract class PluginPlus implements Plugin {
    /**
     * 属性是否隔离
     */
    protected abstract boolean isPropIsolation();
    /**
     * Bean是否隔离
     */
    protected abstract boolean isBeanIsolation();
    /**
     * 启动时
     * */
    protected abstract void onStart(SolonApp app);
    /**
     * 预停止时
     * */
    protected abstract void onPrestop() throws Throwable;
    /**
     * 停止时
     * */
    protected abstract void onStop() throws Throwable;


    private AopContext context;

    /**
     * 上下文（提供隔离支持）
     */
    public AopContext context() {
        if (context == null) {
            if (isBeanIsolation()) {
                if (isPropIsolation()) {
                    context = Aop.context().copy(new Props());
                } else {
                    context = Aop.context().copy();
                }
            } else {
                context = Aop.context();
            }
        }

        return context;
    }

    @Override
    public final void start(SolonApp app) {
        onStart(app);

        if(isBeanIsolation()){
            context().beanLoaded();
        }
    }

    @Override
    public final void prestop() throws Throwable {
        onPrestop();
    }

    @Override
    public final void stop() throws Throwable {
        onStop();
    }

}
