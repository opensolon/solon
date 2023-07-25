package org.noear.solon.admin.server.config;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;

/**
 * @author shaokeyibb
 * @since 2.3
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Solon.app().source().isAnnotationPresent(EnableAdminServer.class) == false) {
            return;
        }

        //启用 ws
        Solon.app().enableWebSocket(true);
        Solon.app().enableWebSocketMvc(false);

        //添加静态资源
        StaticMappings.add("/", new ClassPathStaticRepository("META-INF/solon-admin-server-ui"));

        //扫描bean
        context.beanScan("org.noear.solon.admin.server");

        //调整页面跳转
        Solon.app().get("/", c -> c.forward("/index.html"));
        Solon.app().get("/index.html", c -> c.redirect("/"));
    }
}
