package org.noear.solon.extend.staticfiles;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;

import java.net.URL;
import java.util.*;

/**
 * 静态文件资源处理
 *
 * @author noear
 * @since 1.0
 * */
public class StaticResourceHandler implements Handler {
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String LAST_MODIFIED = "Last-Modified";

    public StaticResourceHandler() {

    }

    @Override
    public void handle(Context ctx) throws Exception {
        if (ctx.getHandled()) {
            return;
        }

        if (MethodType.GET.name.equals(ctx.method()) == false) {
            return;
        }

        String path = ctx.pathNew();

        //找后缀名
        String suffix = findByExtName(path);
        if (Utils.isEmpty(suffix)) {
            return;
        }

        //找内容类型(先用配置的，再用jdk的)
        String conentType = StaticMimes.findByExt(suffix);

        if (Utils.isEmpty(conentType)) {
            conentType = Utils.mime(suffix);
        }

        if (Utils.isEmpty(conentType)) {
            return;
        }

        //找资源
        URL uri = StaticMappings.find(path);

        if (uri == null) {
            return;
        } else {
            ctx.setHandled(true);

            String modified_since = ctx.header("If-Modified-Since");
            String modified_now = modified_time.toString();

            if (modified_since != null && XPluginProp.maxAge() > 0) {
                if (modified_since.equals(modified_now)) {
                    ctx.headerSet(CACHE_CONTROL, "max-age=" + XPluginProp.maxAge());//单位秒
                    ctx.headerSet(LAST_MODIFIED, modified_now);
                    ctx.status(304);
                    return;
                }
            }

            if (XPluginProp.maxAge() > 0) {
                ctx.headerSet(CACHE_CONTROL, "max-age=" + XPluginProp.maxAge());//单位秒
                ctx.headerSet(LAST_MODIFIED, modified_time.toString());
            }

            ctx.contentType(conentType);

            ctx.status(200);
            ctx.output(uri.openStream());
        }
    }

    private static final Date modified_time = new Date();


    private String findByExtName(String path) {
        String ext = "";
        int pos = path.lastIndexOf(35);
        if (pos > 0) {
            path = path.substring(0, pos - 1);
        }

        pos = path.lastIndexOf(46);
        pos = Math.max(pos, path.lastIndexOf(47));
        pos = Math.max(pos, path.lastIndexOf(63));
        if (pos != -1 && path.charAt(pos) == '.') {
            ext = path.substring(pos).toLowerCase();
        }

        return ext;
    }
}
