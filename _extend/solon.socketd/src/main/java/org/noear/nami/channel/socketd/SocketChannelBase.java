package org.noear.nami.channel.socketd;

import org.noear.nami.NamiContext;
import org.noear.nami.NamiManager;
import org.noear.nami.common.Constants;


/**
 * @author noear 2021/1/3 created
 */
public class SocketChannelBase  {
    protected void pretreatment(NamiContext ctx) {
        if (ctx.config.getDecoder() == null) {
            String at = ctx.config.getHeader(Constants.HEADER_ACCEPT);

            if (at == null) {
                at = Constants.CONTENT_TYPE_JSON;
            }

            ctx.config.setDecoder(NamiManager.getDecoder(at));
        }

        if (ctx.config.getEncoder() == null) {
            String ct = ctx.config.getHeader(Constants.HEADER_CONTENT_TYPE);

            if (ct == null) {
                ct = Constants.CONTENT_TYPE_JSON;
            }

            ctx.config.setEncoder(NamiManager.getEncoder(ct));
        }
    }
}
