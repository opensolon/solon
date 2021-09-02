package org.noear.solon.extend.mybatisplus;

import org.noear.solon.extend.mybatis.SqlSessionManager;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        //
        // 此插件的 solon.plugin.priority 会大于 mybatis-solon-plugin 的值
        //
        SqlSessionManager.global().setAdapterFactory(new SqlAdapterFactoryPlus());
    }
}
