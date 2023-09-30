package org.noear.solon.boot.web;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.handle.Context;

import java.io.IOException;
import java.net.URLDecoder;

/**
 * 编码窗体工具类
 *
 * @author noear
 * @since 2.5
 */
public class FormUrlencodedUtils {
    /**
     * 预处理
     * */
    public static void pretreatment(Context ctx) throws IOException {
        if (ctx.isFormUrlencoded() == false) {
            return;
        }

        if (ctx.paramMap().size() > 0) {
            return;
        }

        if (Utils.isEmpty(ctx.bodyNew())) {
            return;
        }

        String[] ss = ctx.bodyNew().split("&");

        for (String s1 : ss) {
            String[] ss2 = s1.split("=");

            if (ss2.length == 2) {
                ctx.paramSet(ss2[0], URLDecoder.decode(ss2[1], ServerProps.request_encoding));
            }
        }
    }
}
