package org.noear.nami;

import org.noear.nami.common.Constants;
import org.noear.nami.common.ContentTypes;

/**
 * 处理通道基类
 *
 * @author noear
 * @since 2.4
 */
public abstract class ChannelBase implements Channel {
    /**
     * 预处理
     *
     * @param ctx 上下文
     */
    protected void pretreatment(Context ctx) {
        if (ctx.config.getDecoder() == null) {
            String at = ctx.config.getHeader(Constants.HEADER_ACCEPT);

            if (at == null) {
                at = ContentTypes.JSON_VALUE;
            }

            ctx.config.setDecoder(NamiManager.getDecoder(at));

            if (ctx.config.getDecoder() == null) {
                ctx.config.setDecoder(NamiManager.getDecoderFirst());
            }
        }

        if (ctx.config.getEncoder() == null) {
            String ct = ctx.config.getHeader(Constants.HEADER_CONTENT_TYPE);

            if (ct == null) {
                ct = ContentTypes.JSON_VALUE;
            }

            ctx.config.setEncoder(NamiManager.getEncoder(ct));

            if (ctx.config.getEncoder() == null) {
                ctx.config.setEncoder(NamiManager.getEncoderFirst());
            }
        }
    }
}