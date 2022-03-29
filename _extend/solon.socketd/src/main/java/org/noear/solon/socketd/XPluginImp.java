package org.noear.solon.socketd;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.annotation.ClientEndpoint;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        //注册 @ClientListenEndpoint 构建器
        Aop.context().beanBuilderAdd(ClientEndpoint.class, (clz, wrap, anno) -> {
            if (Listener.class.isAssignableFrom(clz)) {
                Listener l = wrap.raw();

                //创建会话
                Session s = SocketD.createSession(anno.uri(), anno.autoReconnect());

                //绑定监听
                s.listener(l);

                //发送握手包
                if (Utils.isNotEmpty(anno.handshakeHeader())) {
                    try {
                        s.sendHandshake(Message.wrapHandshake(anno.handshakeHeader()));
                    } catch (Throwable ex) {
                        EventBus.push(ex);
                    }
                }

                //设定自动心跳
                if (anno.heartbeatRate() > 0) {
                    s.sendHeartbeatAuto(anno.heartbeatRate());
                }
            }
        });

        //注册 ListenerPipeline
        Aop.wrapAndPut(ListenerPipeline.class, ListenerManager.getPipeline());
    }
}
