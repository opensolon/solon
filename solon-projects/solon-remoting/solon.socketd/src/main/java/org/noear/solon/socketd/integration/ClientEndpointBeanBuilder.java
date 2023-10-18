package org.noear.solon.socketd.integration;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.SocketD;
import org.noear.solon.socketd.annotation.ClientEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 2.5
 */
public class ClientEndpointBeanBuilder implements BeanBuilder<ClientEndpoint> {
    static final Logger log = LoggerFactory.getLogger(ClientEndpointBeanBuilder.class);

    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, ClientEndpoint anno) throws Throwable {
        if (Listener.class.isAssignableFrom(clz)) {
            Listener l = bw.raw();

            //创建会话
            Session s = SocketD.createSession(anno.uri(), anno.autoReconnect());

            //绑定监听
            s.listener(l);

            //发送握手包
            if (Utils.isNotEmpty(anno.handshakeHeader())) {
                try {
                    s.sendHandshake(Message.wrapHandshake(anno.handshakeHeader()));
                } catch (Throwable e) {
                    log.warn(e.getMessage() ,e);
                }
            }

            //设定自动心跳
            if (anno.heartbeatRate() > 0) {
                s.startHeartbeatAuto(anno.heartbeatRate());
            }
        }
    }
}
