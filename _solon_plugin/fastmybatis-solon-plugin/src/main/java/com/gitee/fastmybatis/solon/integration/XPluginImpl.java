package com.gitee.fastmybatis.solon.integration;

import org.apache.ibatis.solon.integration.MybatisAdapterManager;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author thc
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) {
        MybatisAdapterManager.setAdapterFactory(new MybatisAdapterFactoryFastmybatis());
    }
}
