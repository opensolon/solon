package org.noear.solon.cloud.extend.cloudeventplus;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.model.Event;

/**
 * Cloud event 实体
 *
 * <pre><code>
 * //申明事件实体
 * @CloudEvent("user.create.event")
 * public class UserCreatedEvent implements CloudEventEntity{
 *     public long userId;
 * }
 *
 * UserCreatedEvent event = new UserCreatedEvent();
 * event.userId = 1;
 *
 * //发布事件
 * CloudClient.event().publish(event);
 *
 * //订阅事件
 * public class UserCreatedEventHandler implements CloudEventEntityHandler<UserCreatedEvent>{
 *     public boolean handler(UserCreatedEvent event) throws Throwable{
 *         //
 *         return true;
 *     }
 * }
 *
 * </code></pre>
 *
 * @author 颖
 * @since 1.5
 */
public interface CloudEventEntity {
    /**
     * 发布事件
     */
    default boolean publish() throws CloudEventException {
        CloudEvent anno2 = this.getClass().getAnnotation(CloudEvent.class);

        if (anno2 == null) {
            throw new IllegalArgumentException("The entity is missing (@CloudEvent) annotations: " + this.getClass().getName());
        }

        String topic = Utils.annoAlias(anno2.value(), anno2.topic());
        String content = ONode.stringify(this);

        return CloudClient.event().publish(new Event(topic, content).channel(anno2.channel()).group(anno2.group()));
    }
}
