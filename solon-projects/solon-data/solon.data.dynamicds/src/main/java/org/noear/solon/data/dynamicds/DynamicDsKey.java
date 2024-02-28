package org.noear.solon.data.dynamicds;

import org.noear.solon.core.FactoryManager;

/**
 * 动态数据源 Key 管理
 *
 * @author noear
 * @since 2.5
 */
public class DynamicDsKey {
    static ThreadLocal<String> targetThreadLocal = FactoryManager.newThreadLocal(false);

    /**
     * 移除状态
     */
    public static void remove() {
        targetThreadLocal.remove();
    }

    /**
     * 获取当前
     */
    public static String getCurrent() {
        return targetThreadLocal.get();
    }

    /**
     * 设置当前
     */
    public static void setCurrent(String name) {
        if (name == null) {
            targetThreadLocal.remove();
        } else {
            targetThreadLocal.set(name);
        }
    }
}
