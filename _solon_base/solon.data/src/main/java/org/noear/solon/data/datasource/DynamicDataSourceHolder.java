package org.noear.solon.data.datasource;

/**
 * 动态数据源切换
 *
 * @author noear
 * @since 1.10
 */
public class DynamicDataSourceHolder {
    static ThreadLocal<String> targetThreadLocal = new ThreadLocal<>();

    /**
     * 清除
     * */
    public static void clear(){
        targetThreadLocal.remove();
    }

    /**
     * 获取
     * */
    public static String get(){
        return targetThreadLocal.get();
    }

    /**
     * 设置
     * */
    public static void set(String dsBeanName){
        targetThreadLocal.set(dsBeanName);
    }
}
