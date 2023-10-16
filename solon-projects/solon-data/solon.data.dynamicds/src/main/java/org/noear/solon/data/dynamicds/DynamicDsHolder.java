package org.noear.solon.data.dynamicds;


/**
 * 动态数据源切换
 *
 * @author noear
 * @since 1.11
 * @deprecated 2.5
 */
@Deprecated
public class DynamicDsHolder {
    /**
     * 清除
     */
    @Deprecated
    public static void remove() {
        DynamicDsKey.remove();
    }

    /**
     * 获取
     */
    @Deprecated
    public static String get() {
        return DynamicDsKey.getCurrent();
    }

    /**
     * 设置
     */
    @Deprecated
    public static void set(String name) {
        DynamicDsKey.setCurrent(name);
    }
}
