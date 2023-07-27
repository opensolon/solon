package org.noear.solon.core.route;

import org.noear.solon.annotation.Note;

/**
 * 路径限制器
 *
 * @author noear
 * @since 2.3
 * @deprecated 2.4
 */
@Deprecated
public interface PathLimiter {
    /**
     * 路径规则
     *
     * @deprecated 2.4
     * */
    @Note("改用：RouterInterceptor:pathPatterns")
    @Deprecated
    PathRule pathRule();
}
