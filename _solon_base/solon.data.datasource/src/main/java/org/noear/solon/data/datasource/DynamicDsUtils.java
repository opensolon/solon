package org.noear.solon.data.datasource;

/**
 * 动态数据源切换
 *
 * @author noear
 * @since 1.11
 */
public class DynamicDsUtils {
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
    public static String getCurrent() {
        return targetThreadLocal.get();
    }

    /**
     * 设置
     */
    public static void setCurrent(String dsBeanName) {
        if (dsBeanName == null) {
            targetThreadLocal.remove();
        } else {
            targetThreadLocal.set(dsBeanName);
        }
    }
}
