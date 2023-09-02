package org.noear.solon.admin.server.config;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.admin.server.annotation.EnableAdminServer;
import org.noear.solon.admin.server.utils.BasicAuthUtils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;

/**
 * @author shaokeyibb
 * @since 2.3
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) {
        if (Solon.app().source().isAnnotationPresent(EnableAdminServer.class) == false) {
            return;
        }

        //1.启用 ws
        Solon.app().enableWebSocket(true);
        Solon.app().enableWebSocketMvc(false);

        //2.扫描bean
        context.beanScan("org.noear.solon.admin.server");

        //3.分享管理地址（如果与客户端一起用，则能被客户端发现）
        String serverUrl = "http://localhost:" + Solon.cfg().serverPort();
        Solon.app().sharedAdd("solon-admin-server-url", serverUrl);

        //4.确定界面资源
        ServerProperties serverProperties = context.getBean(ServerProperties.class);
        String uiPath = buildUiPath(serverProperties);

        //添加静态资源
        StaticMappings.add(uiPath, new ClassPathStaticRepository("META-INF/solon-admin-server-ui"));

        //添加签权
        Solon.app().before(uiPath, ctx -> {
            if (!BasicAuthUtils.basicAuth(ctx, serverProperties)) {
                BasicAuthUtils.response401(ctx);
                ctx.setHandled(true);
            }
        });
        //添加跳转
        Solon.app().get(uiPath, c -> c.forward(uiPath + "index.html"));
        Solon.app().get(uiPath + "index.html", c -> c.redirect(uiPath));
    }

    private String buildUiPath(ServerProperties serverProperties) {
        String uiPath = serverProperties.getUiPath();
        if (Utils.isEmpty(uiPath)) {
            uiPath = "/";
        }

        if (uiPath.startsWith("/") == false) {
            uiPath = "/" + uiPath;
        }

        if (uiPath.endsWith("/") == false) {
            uiPath = uiPath + "/";
        }

        return uiPath;
    }
}
