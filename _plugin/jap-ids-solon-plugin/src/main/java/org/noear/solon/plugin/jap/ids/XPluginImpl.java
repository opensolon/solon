package org.noear.solon.plugin.jap.ids;

import com.fujieid.jap.ids.JapIds;
import com.fujieid.jap.ids.context.IdsContext;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.plugin.jap.ids.http.controller.ApprovalController;
import org.noear.solon.plugin.jap.ids.managers.RouterManager;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImpl implements Plugin {

    @Override
    public void start(SolonApp app) {
        // 不实现和注入任何 Service, 因为 Jap Ids 会调用
        // ServiceLoader.load 方法, 这样方便用户实现后自动注入
        // 还能很好地避免冲突
        IdsContext context = new IdsContext()
                .setIdsConfig(IdsProps.idsConfig);

        JapIds.registerContext(context);

        Aop.wrapAndPut(IdsContext.class, context);

        new RouterManager(app);
    }

}
