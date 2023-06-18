package org.noear.solon.core.handle;

/**
 * 动作返回处理器
 *
 * @author noear
 * @since 2.3
 */
public interface ActionReturnHandler {
    /**
     * 处理
     * */
    void handle(Context ctx, Action action, Object result) throws Throwable;
}
