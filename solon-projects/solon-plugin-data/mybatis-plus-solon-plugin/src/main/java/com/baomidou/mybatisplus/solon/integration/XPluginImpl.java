package com.baomidou.mybatisplus.solon.integration;

import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.solon.integration.aot.MybatisPlusRuntimeNativeRegistrar;
import org.apache.ibatis.solon.aot.MybatisRuntimeNativeRegistrar;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.GenericUtil;
import org.apache.ibatis.solon.integration.MybatisAdapterManager;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) {
        //
        // 此插件的 solon.plugin.priority 会大于 mybatis-solon-plugin 的值
        //
        MybatisAdapterManager.setAdapterFactory(new MybatisAdapterFactoryPlus());
        GenericTypeUtils.setGenericTypeResolver(((clazz, genericIfc) ->
                GenericUtil.resolveTypeArguments(clazz, genericIfc)));

        // aot
        if (NativeDetector.isAotRuntime() && ClassUtil.hasClass(() -> RuntimeNativeRegistrar.class)) {
            context.wrapAndPut(MybatisPlusRuntimeNativeRegistrar.class);
        }

    }
}
