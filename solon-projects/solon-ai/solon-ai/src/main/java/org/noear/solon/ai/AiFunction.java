package org.noear.solon.ai;

import java.util.Map;

/**
 * @author noear 2025/2/6 created
 */
public interface AiFunction<P extends AiFunctionParam> {
    /**
     * 名字
     */
    String name();

    /**
     * 描述
     */
    String description();

    /**
     * 参数
     */
    Iterable<P> params();

    /**
     * 处理
     */
    Object handle(Map<String, Object> args);
}
