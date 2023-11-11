package org.noear.solon.net.websocket;

import org.noear.socketd.utils.Utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 握手信息实现类
 *
 * @author noear
 * @since 2.6
 */
public class HandshakeImpl implements Handshake {
    private final String url;
    private final URI uri;
    private final Map<String, String> paramMap;

    public HandshakeImpl(URI uri) {
        this.uri = uri;
        this.url = uri.toString();
        this.paramMap = new HashMap<>();

        String queryString = uri.getQuery();
        if (Utils.isNotEmpty(queryString)) {
            for (String kvStr : queryString.split("&")) {
                int idx = kvStr.indexOf('=');
                if (idx > 1) {
                    paramMap.put(kvStr.substring(0, idx), kvStr.substring(idx + 1));
                }
            }
        }
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }
}
