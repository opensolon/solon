package org.noear.solon.extend.mybatisplus.integration;

import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.extend.mybatis.integration.MybatisAdapterManager;
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
        MybatisAdapterManager.setAdapterFactory(new MybatisAdapterFactoryPlus());
        GenericTypeUtils.setGenericTypeResolver(((clazz, genericIfc) ->
                GenericUtil.resolveTypeArguments(clazz, genericIfc)));

    }
}
