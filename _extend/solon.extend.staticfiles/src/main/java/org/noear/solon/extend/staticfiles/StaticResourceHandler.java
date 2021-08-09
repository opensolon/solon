package org.noear.solon.extend.staticfiles;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;

import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class StaticResourceHandler implements Handler {
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String LAST_MODIFIED = "Last-Modified";

    private StaticMappings staticMappings = StaticMappings.instance();
    private StaticFiles staticFiles = StaticFiles.instance();
    private Pattern _rule;

    public StaticResourceHandler() {
        String expr = "(" + String.join("|", staticFiles.keySet()) + ")$";

        _rule = Pattern.compile(expr, Pattern.CASE_INSENSITIVE);
    }


    @Override
    public void handle(Context ctx) throws Exception {
        if (ctx.getHandled()) {
            return;
        }

        if (MethodType.GET.name.equals(ctx.method()) == false) {
            return;
        }

        if (_rule.matcher(ctx.path()).find() == false) {
            return;
        }

        String path = ctx.path();

        URL uri = staticMappings.find(path);

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
                    ctx.statusSet(304);
                    return;
                }
            }

            int idx = path.lastIndexOf(".");
            if (idx > 0) {
                String suffix = path.substring(idx);
                String mime = staticFiles.get(suffix);

                if (mime != null) {
                    if (XPluginProp.maxAge() > 0) {
                        ctx.headerSet(CACHE_CONTROL, "max-age=" + XPluginProp.maxAge());//单位秒
                        ctx.headerSet(LAST_MODIFIED, modified_time.toString());
                    }

                    ctx.contentType(mime);
                }
            }

            ctx.statusSet(200);
            ctx.output(uri.openStream());
        }
    }

    private static final Date modified_time = new Date();
}
