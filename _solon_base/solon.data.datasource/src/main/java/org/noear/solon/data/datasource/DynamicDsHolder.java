package org.noear.solon.data.datasource;

/**
 * 动态数据源切换
 *
 * @author noear
 * @since 1.11
 */
public class DynamicDsHolder {
    static ThreadLocal<String> targetThreadLocal = new ThreadLocal<>();

    /**
     * 清除
     */
    public static void clear() {
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
    public static void set(String dsBeanName) {
        if (dsBeanName == null) {
            targetThreadLocal.remove();
        } else {
            targetThreadLocal.set(dsBeanName);
        }
    }
}
