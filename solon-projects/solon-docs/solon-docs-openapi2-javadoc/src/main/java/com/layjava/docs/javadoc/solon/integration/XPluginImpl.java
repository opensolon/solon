package com.layjava.docs.javadoc.solon.integration;

import com.layjava.docs.javadoc.solon.controller.OpenApiController;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

public class XPluginImpl implements Plugin {

    /**
     * 启动
     *
     * @param context 应用上下文
     */
    @Override
    public void start(AppContext context) throws Throwable {
        // 注册路由
        Solon.app().add("/", OpenApiController.class);
    }

}
