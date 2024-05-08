package org.noear.solon.boot.web;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.handle.Context;

import java.io.IOException;

/**
 * 编码窗体工具类
 *
 * @author noear
 * @since 2.5
 */
public class FormUrlencodedUtils {
    /**
     * 预处理
     */
    public static void pretreatment(Context ctx) throws IOException {
        if (ctx.isFormUrlencoded() == false) {
            return;
        }

        if (Utils.isEmpty(ctx.bodyNew())) {
            return;
        }

        String[] ss = ctx.bodyNew().split("&");

        for (String s1 : ss) {
            int idx = s1.indexOf('=');
            if (idx > 0) {
                String name = ServerProps.urlDecode(s1.substring(0, idx));
                String value = ServerProps.urlDecode(s1.substring(idx + 1));
                ctx.paramSet(name, value);
            }
        }
    }
}
