package org.noear.solon.annotation;

import org.noear.solon.core.XMethod;

import java.lang.annotation.*;

/**
 * 服务器处理端点
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
public @interface XServerEndpoint {
    /**
     * 资源描述
     * */
    String value() default "";
    /**
     * 方法
     * */
    XMethod method() default XMethod.ALL;
}
