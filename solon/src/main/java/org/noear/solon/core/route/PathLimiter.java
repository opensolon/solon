package org.noear.solon.core.route;

/**
 * 路径限制器
 *
 * @author noear
 * @since 2.3
 */
public interface PathLimiter {
    /**
     * 路径规则
     * */
    PathRule pathRule();
}
