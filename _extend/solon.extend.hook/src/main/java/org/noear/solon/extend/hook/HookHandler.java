package org.noear.solon.extend.hook;

import java.util.Map;

/**
 * 勾子处理
 *
 * @author noear
 * @since 1.8
 */
public interface HookHandler {
    void onBefore(Map<String, Object> args);

    void onAfter(Map<String, Object> args);
}
