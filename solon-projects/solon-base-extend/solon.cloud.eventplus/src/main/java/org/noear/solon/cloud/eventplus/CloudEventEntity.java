package org.noear.solon.cloud.eventplus;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.model.Event;

/**
 * 云端事件实体
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
 *     public boolean handle(UserCreatedEvent event) throws Throwable{
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

        //支持${xxx}配置
        String topic2 = Solon.cfg().getByParse(Utils.annoAlias(anno2.value(), anno2.topic()));
        //支持${xxx}配置
        String group2 = Solon.cfg().getByParse(anno2.group());

        String content = ONode.stringify(this);

        return CloudClient.event().publish(new Event(topic2, content).group(group2).tags(anno2.tag()).channel(anno2.channel()));
    }
}
