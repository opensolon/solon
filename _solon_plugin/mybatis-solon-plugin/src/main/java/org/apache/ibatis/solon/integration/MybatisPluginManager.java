package org.apache.ibatis.solon.integration;

import org.apache.ibatis.plugin.Interceptor;
import org.noear.solon.Solon;

import java.util.List;

/**
 * 插件管理器
 *
 * @author noear
 * @since 1.5
 */
public class MybatisPluginManager {
    private static List<Interceptor> interceptors;

    public static List<Interceptor> getInterceptors() {
        tryInit();

        return interceptors;
    }

    private static void tryInit() {
        if (interceptors != null) {
            return;
        }

        //支持两种配置
        interceptors = MybatisPluginUtils.resolve(Solon.cfg(), "mybatis.plugin");
        if(interceptors.size() == 0){
            //新加
            interceptors = MybatisPluginUtils.resolve(Solon.cfg(), "mybatis.plugins");
        }
    }
}
