package com.mybatisflex.solon.integration;

import org.apache.ibatis.solon.integration.MybatisAdapterManager;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * 配置 MyBatis-Flex 插件。
 *
 * @author noear
 * @since 2.2
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        // 此插件的 solon.plugin.priority 会大于 mybatis-solon-plugin 的值
        MybatisAdapterManager.setAdapterFactory(new MybatisAdapterFactoryFlex());
    }
}
