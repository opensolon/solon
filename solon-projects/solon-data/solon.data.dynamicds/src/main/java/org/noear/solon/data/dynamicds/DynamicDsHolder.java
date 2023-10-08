package org.noear.solon.data.dynamicds;

import org.noear.solon.core.util.ThreadUtil;

/**
 * 动态数据源切换
 *
 * @author noear
 * @since 1.11
 */
public class DynamicDsHolder {
    static ThreadLocal<String> targetThreadLocal = ThreadUtil.global().newThreadLocal(false);

    /**
     * 清除
     */
    public static void remove() {
        targetThreadLocal.remove();
    }

    /**
     * 获取
     */
    public static String get() {
        return targetThreadLocal.get();
    }

    /**
     * 设置
     */
    public static void set(String name) {
        if (name == null) {
            targetThreadLocal.remove();
        } else {
            targetThreadLocal.set(name);
        }
    }
}
