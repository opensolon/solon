package org.noear.solon.cloud.eventplus.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.eventplus.CloudEventSubscribe;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author noear
 * @author 颖
 * @since 1.5
 */
public class CloudEventSubscribeBeanExtractor implements BeanExtractor<CloudEventSubscribe> {

    @Override
    public void doExtract(BeanWrap bw, Method method, CloudEventSubscribe anno) {
        if (CloudClient.event() == null) {
            throw new IllegalArgumentException("Missing CloudEventService component");
        }

        Parameter[] args = method.getParameters();
        if (args.length != 1) {
            throw new IllegalArgumentException("Missing CloudEventSubscriber method need one parameter");
        }

        Class<?> entityClz = args[0].getType();

        CloudEvent anno2 = entityClz.getAnnotation(CloudEvent.class);

        if (anno2 == null) {
            throw new IllegalArgumentException("The entity is missing (@CloudEvent) annotations: " + this.getClass().getName());
        }

        //支持${xxx}配置
        String topic2 = Solon.cfg().getByParse(Utils.annoAlias(anno2.value(), anno2.topic()));
        //支持${xxx}配置
        String group2 = Solon.cfg().getByParse(anno2.group());

        CloudEventMethodProxy hadnler2 = new CloudEventMethodProxy(bw.raw(), method, entityClz);

        CloudManager.register(anno2, hadnler2);
        CloudClient.event().attention(anno2.level(), anno2.channel(), group2, topic2, anno2.tag(), hadnler2);
    }
}
