package org.noear.solon.admin.server.config;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
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

        //1.启用 ws
        Solon.app().enableWebSocket(true);
        Solon.app().enableWebSocketMvc(false);

        //2.扫描bean
        context.beanScan("org.noear.solon.admin.server");

        //3.分享管理地址（如果与客户端一起用，则能被客户端发现）
        String serverUrl = "http://localhost:" + Solon.cfg().serverPort();
        Solon.app().sharedAdd("solon-admin-server-url", serverUrl);

        //4.确定界面资源
        String adminPath = buildAdminPath(context);

        //添加静态资源
        StaticMappings.add(adminPath, new ClassPathStaticRepository("META-INF/solon-admin-server-ui"));

        //添加跳转
        Solon.app().get(adminPath, c -> c.forward(adminPath+"index.html"));
        Solon.app().get(adminPath +"index.html", c -> c.redirect(adminPath));
    }

    private String buildAdminPath(AopContext context){
        String adminPath = context.getBean(ServerProperties.class).getUiPath();
        if (Utils.isEmpty(adminPath)) {
            adminPath = "/";
        }

        if (adminPath.startsWith("/") == false) {
            adminPath = "/" + adminPath;
        }

        if(adminPath.endsWith("/") == false){
            adminPath = adminPath+"/";
        }

        return adminPath;
    }
}
