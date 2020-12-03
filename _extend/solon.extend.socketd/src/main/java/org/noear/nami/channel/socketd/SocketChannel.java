package org.noear.nami.channel.socketd;


import org.noear.nami.NamiChannel;
import org.noear.nami.NamiConfig;
import org.noear.nami.Result;
import org.noear.nami.channel.Constants;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.core.util.HeaderUtils;
import org.noear.solon.extend.socketd.MessageWrapper;
import org.noear.solon.extend.socketd.annotation.Handshake;

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
        String message_key = Utils.guid();
        int flag = 0;

        if (method != null) {
            Handshake handshake = method.getAnnotation(Handshake.class);
            if (handshake != null) {
                flag = -1;

                if (Utils.isNotEmpty(handshake.handshakeHeader())) {
                    Map<String, String> headerMap = HeaderUtils.decodeHeaderMap(handshake.handshakeHeader());
                    headers.putAll(headerMap);
                }
            }
        }


        //1.执行并返回

        switch (cfg.getEncoder().enctype()) {
            case application_hessian: {
                headers.put("Content-Type", Constants.ct_hessian);
                message = new Message(flag, message_key, url, HeaderUtils.encodeHeaderMap(headers), (byte[]) cfg.getEncoder().encode(args));
                break;
            }
            default: {
                // 默认：application_json
                //
                headers.put("Content-Type", Constants.ct_json);
                String json = (String) cfg.getEncoder().encode(args);
                message = new Message(flag, message_key, url, HeaderUtils.encodeHeaderMap(headers), json.getBytes());
                break;
            }
        }

        if (Solon.cfg().isFilesMode()) {
            System.out.println(message.toString());
        }

        Message response = sessions.get().sendAndResponse(message);

        if (response == null) {
            return null;
        }

        //2.构建结果
        Result result = new Result(200, response.body());

        //3.返回结果
        return result;
    }
}
