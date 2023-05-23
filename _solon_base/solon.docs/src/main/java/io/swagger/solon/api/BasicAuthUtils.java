package io.swagger.solon.api;

import io.swagger.solon.models.SwaggerDocket;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;

import java.io.IOException;

/**
 * @author noear
 * @since 2.3
 */
public class BasicAuthUtils {
    /**
     * WWW-Authenticate 简单认证
     */
    public static boolean basicAuth(Context ctx, SwaggerDocket docket) throws IOException {
        if (docket.basicAuth() == null || docket.basicAuth().size() == 0) {
            // 未启用简单认证
            return true;
        }

        String authorization = ctx.header("Authorization");
        if (Utils.isBlank(authorization)) {
            // 请求头无认证信息
            return false;
        }


        String nameAndPwd = Base64Utils.decodeToStr(authorization.substring(6));
        String[] upArr = nameAndPwd.split(":");

        if (upArr.length != 2) {
            return false;
        }
        String iptName = upArr[0];
        String iptPwd = upArr[1];

        return iptPwd.equals(docket.basicAuth().get(iptName));
    }

    public static void response401(Context ctx) throws IOException {
        ctx.status(401);
        ctx.headerSet("WWW-Authenticate", "Basic realm=\"请输入Swagger文档访问账号密码\"");
        ctx.output("无权限访问");
    }
}
