package org.noear.solon.annotation;

import org.noear.solon.core.handle.MethodType;

import java.lang.annotation.*;

/**
 * 服务器处理端点（为 XMessage + XListener 架构，提供容器化支持）
 *
 * <pre><code>
 * @XServerEndpoint(value = "/demoe/websocket")
 * public class XSocketListenerImp implements XListener {
 *     @Override
 *     public void onMessage(XSession session, XMessage message) {
 *         //表示已处理
 *         message.setHandled(true);
 *
 *         //把消息内容广播给别的会话
 *         session.getOpenSessions().forEach(s -> {
 *             s.send(message.toString());
 *         });
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServerEndpoint {
    /**
     * 资源描述
     * */
    String value() default "";
    /**
     * 方法
     * */
    MethodType method() default MethodType.ALL;
}
