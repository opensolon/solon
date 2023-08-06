package org.noear.solon.admin.server.utils;

import org.noear.solon.Utils;
import org.noear.solon.admin.server.config.ServerProperties;
import org.noear.solon.core.handle.Context;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * @author noear
 * @since 2.3
 */
public class BasicAuthUtils {
    static final Charset UTF_8 = Charset.forName("UTF-8");

    public static String base64DecodeToStr(String value) {
        byte[] decodedValue = Base64.getDecoder().decode(value);
        return new String(decodedValue, UTF_8);
    }

    /**
     * WWW-Authenticate 简单认证
     */
    public static boolean basicAuth(Context ctx, ServerProperties serverProperties) throws IOException {
        if (serverProperties.getBasicAuth() == null || serverProperties.getBasicAuth().size() == 0) {
            // 未启用简单认证
            return true;
        }

        String authorization = ctx.header("Authorization");
        if (Utils.isBlank(authorization)) {
            // 请求头无认证信息
            return false;
        }


        String nameAndPwd = base64DecodeToStr(authorization.substring(6));
        String[] upArr = nameAndPwd.split(":");

        if (upArr.length != 2) {
            return false;
        }
        String iptName = upArr[0];
        String iptPwd = upArr[1];

        return iptPwd.equals(serverProperties.getBasicAuth().get(iptName));
    }

    public static void response401(Context ctx) throws IOException {
        ctx.status(401);
        ctx.headerSet("WWW-Authenticate", "Basic realm=\"请输入Solon-Admin访问账号密码\"");
        ctx.output("无权限访问");
    }
}
