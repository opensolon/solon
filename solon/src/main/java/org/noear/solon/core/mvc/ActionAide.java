package org.noear.solon.core.mvc;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.Handler;

/**
 * mvc:动作接口助理
 *
 * @author noear
 * @since 2.9
 */
public interface ActionAide extends Action {
    /**
     * 添加前置处理
     *
     * @deprecated 2.9
     */
    @Deprecated
    void before(Handler handler);

    /**
     * 添加后置处理
     *
     * @deprecated 2.9
     */
    @Deprecated
    void after(Handler handler);

    /**
     * 添加过滤处理
     *
     * @since 2.9
     */
    void filter(int index, Filter filter);
}
