package org.noear.nami.channel.socketd;


import org.noear.nami.*;
import org.noear.nami.channel.Constants;
import org.noear.solon.Utils;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.core.message.MessageFlag;
import org.noear.solon.socketd.annotation.Handshake;
import org.noear.solon.socketd.util.HeaderUtil;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Supplier;

public class SocketChannel implements NamiChannel {
    public Supplier<Session> sessions;

    public SocketChannel(Supplier<Session> sessions) {
        this.sessions = sessions;
    }

    //public SocketChannel handshake();

    @Override
    public Result call(NamiConfig cfg, Method method, String action, String url, Map<String, String> headers, Map<String, Object> args) throws Throwable {

        //0.尝试解码器的过滤
        cfg.getDecoder().filter(cfg, action, url, headers, args);

        Message message = null;
        String message_key = Message.guid();
        int flag = MessageFlag.message;

        if (method != null) {
            //是否为握手
            //
            Handshake h = method.getAnnotation(Handshake.class);
            if (h != null) {
                flag = MessageFlag.handshake;

                if (Utils.isNotEmpty(h.handshakeHeader())) {
                    Map<String, String> headerMap = HeaderUtil.decodeHeaderMap(h.handshakeHeader());
                    headers.putAll(headerMap);
                }
            }
        }

        //1.确定编码器
        Encoder encoder = cfg.getEncoder();
        if(encoder == null){
            encoder = NamiManager.getEncoder(Constants.ct_json);
        }

        if(encoder == null){
            throw new IllegalArgumentException("There is no suitable encoder");
        }

        //2.构建消息
        headers.put(Constants.h_content_type, encoder.enctype().contentType);
        byte[] bytes = encoder.encode(args);
        message = new Message(flag, message_key, url, HeaderUtil.encodeHeaderMap(headers), bytes);

        //3.发送消息
        Message res = sessions.get().sendAndResponse(message);

        if (res == null) {
            return null;
        }

        //2.构建结果
        Result result = new Result(200, res.body());

        //2.1.设置头
        if (Utils.isNotEmpty(res.header())) {
            Map<String, String> map = HeaderUtil.decodeHeaderMap(res.header());
            map.forEach((k, v) -> {
                result.headerAdd(k, v);
            });
        }

        //3.返回结果
        return result;
    }
}
