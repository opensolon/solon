package org.noear.solon.cloud.trace;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.net.http.HttpExtension;
import org.noear.solon.net.http.HttpExtensionManager;
import org.noear.solon.net.http.HttpUtils;

/**
 * Http 跟踪扩展
 *
 * @author noear
 * @since 2.9
 */
public class HttpTraceExtension implements HttpExtension {
    /**
     * 注册扩展
     */
    public static void register() {
        HttpExtensionManager.add(new HttpTraceExtension());
    }

    @Override
    public void onInit(HttpUtils httpUtils, String url) {
        if (CloudClient.trace() != null) {
            httpUtils.header(CloudClient.trace().HEADER_TRACE_ID_NAME(), CloudClient.trace().getTraceId());
            httpUtils.header(CloudClient.trace().HEADER_FROM_ID_NAME(), CloudClient.trace().getFromId());
        }
    }
}
