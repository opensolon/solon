package org.noear.nami.channel.socketd;

import org.noear.nami.Filter;
import org.noear.nami.NamiConfig;
import org.noear.nami.NamiManager;
import org.noear.nami.channel.Constants;

import java.util.Map;

/**
 * @author noear 2021/1/3 created
 */
public class SocketChannelFilter implements Filter {
    @Override
    public void filter(NamiConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) {
        if (cfg.getDecoder() == null) {
            String at = cfg.getHeader(Constants.h_accept);

            if (at == null) {
                at = Constants.ct_json;
            }

            cfg.setDecoder(NamiManager.getDecoder(at));
        }

        if (cfg.getEncoder() == null) {
            String ct = cfg.getHeader(Constants.h_content_type);

            if (ct == null) {
                ct = Constants.ct_json;
            }

            cfg.setEncoder(NamiManager.getEncoder(ct));
        }
    }
}
