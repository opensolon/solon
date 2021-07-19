package org.noear.solon.annotation;

import org.noear.solon.core.handle.MethodType;

import java.lang.annotation.*;

/**
 * 监听处理端点（为 Message + Listener 架构，提供容器化支持）
 *
 * <pre><code>
 * @ServerEndpoint(value = "/demoe/websocket")
 * public class SocketListenerImp implements Listener {
 *     @Override
 *     public void onMessage(Session session, Message message) {
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
@Alias(anno = Component.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServerEndpoint {
    @Alias("path")
    String value() default "";

    @Alias("value")
    String path() default "";

    /**
     * 方法
     */
    MethodType method() default MethodType.ALL;
}
