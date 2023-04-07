package com.mybatisflex.solon.integration;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.apache.ibatis.solon.integration.MybatisAdapterManager;

/**
 * @author noear 2023/4/7 created
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        //
        // 此插件的 solon.plugin.priority 会大于 mybatis-solon-plugin 的值
        //
        MybatisAdapterManager.setAdapterFactory(new MybatisAdapterFactoryFlex());
    }
}
