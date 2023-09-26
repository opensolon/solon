package org.noear.solon.web.staticfiles;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;

import java.util.HashSet;
import java.util.Set;

/**
 * 静态文件压缩配置
 *
 * @author noear
 * @since 2.5
 */
public class StaticCompression {
    public static boolean enable = false;
    public static long minSize = 4096;
    public static Set<String> mimeTypes = new HashSet<>();

    static {
        mimeTypes.add("text/html");
        mimeTypes.add("text/xml");
        mimeTypes.add("text/plain");
        mimeTypes.add("text/css");
        mimeTypes.add("text/javascript");
        mimeTypes.add("application/javascript");
        mimeTypes.add("application/xml");
    }

    /**
     * 加载配置
     */
    public static void load() {
        enable = Solon.cfg().getBool("server.http.gzip.enable", false);
        minSize = Solon.cfg().getLong("server.http.gzip.minSize", 4096);
        Solon.cfg().getMap("server.http.gzip.mimeTypes").forEach((key, val) -> {
            for (String mime : val.split(",")) {
                mimeTypes.add(mime);
            }
        });
    }

    /**
     * 处理压缩
     */
    public static boolean handle(Context ctx, String mime, long size) {
        if (enable && size >= minSize) {
            if (mimeTypes.contains(mime.split(";")[0])) {
                ctx.headerSet("Vary", "Accept-Encoding");
                ctx.headerSet("Content-Encoding", "gzip");
                return true;
            }
        }

        return false;
    }
}
