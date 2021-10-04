package org.noear.solon.cloud.utils.http;

import org.noear.solon.Solon;
import org.noear.solon.ext.ConsumerEx;

/**
 * 预热工具
 *
 * @author noear
 * @since 1.5
 */
public final class PreheatUtils {

    /**
     * 预热本地地址
     */
    public static void preheat(String path) {
        preheat(path, h -> h.get());
    }

    /**
     * 预热本地地址
     */
    public static void preheat(String path, ConsumerEx<HttpUtils> handling) {
        try {
            HttpUtils http = HttpUtils.http("http://localhost:" + Solon.global().port() + path);
            handling.accept(http);
            System.out.println("[Preheat] " + path + " : preheat succeeded");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
