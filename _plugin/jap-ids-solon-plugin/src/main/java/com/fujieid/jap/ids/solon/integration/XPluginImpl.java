package com.fujieid.jap.ids.solon.integration;

import com.fujieid.jap.ids.JapIds;
import com.fujieid.jap.ids.context.IdsContext;
import com.fujieid.jap.ids.solon.IdsProps;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import com.fujieid.jap.ids.solon.http.ErrorFilter;
import com.fujieid.jap.ids.solon.http.controller.*;

/**
 * @since 颖
 * @author noear
 * @since 1.6
 */
public class XPluginImpl implements Plugin {

    @Override
    public void start(AopContext context) {
        // 不实现和注入任何 Service, 因为 Jap Ids 会调用
        // ServiceLoader.load 方法, 这样方便用户实现后自动注入
        // 还能很好地避免冲突
        IdsContext idsContext = new IdsContext()
                .setIdsConfig(IdsProps.IDS_CONFIG);

        JapIds.registerContext(idsContext);

        Aop.wrapAndPut(IdsContext.class, context);

        //添加控制器
        Solon.global().add(IdsProps.BAST_PATH, ApprovalController.class);
        Solon.global().add(IdsProps.BAST_PATH, AuthorizationController.class);
        Solon.global().add(IdsProps.BAST_PATH, CheckSessionController.class);
        Solon.global().add(IdsProps.BAST_PATH, ErrorController.class);
        Solon.global().add(IdsProps.BAST_PATH, LoginController.class);
        Solon.global().add(IdsProps.BAST_PATH, LogoutController.class);
        Solon.global().add(IdsProps.BAST_PATH, TokenController.class);
        Solon.global().add(IdsProps.BAST_PATH, UserController.class);

        Solon.global().add(IdsProps.WELL_PATH, DiscoveryController.class);

        //添加过滤器
        Solon.global().filter(new ErrorFilter());
    }

}
