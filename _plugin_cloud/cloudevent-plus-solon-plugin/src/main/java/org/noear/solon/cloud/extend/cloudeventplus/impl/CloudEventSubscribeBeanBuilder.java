package org.noear.solon.cloud.extend.cloudeventplus.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.extend.cloudeventplus.CloudEventEntityHandler;
import org.noear.solon.cloud.extend.cloudeventplus.CloudEventSubscribe;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.util.GenericUtil;

/**
 * @author noear
 * @author 颖
 * @since 1.5
 */
public class CloudEventSubscribeBeanBuilder implements BeanBuilder<CloudEventSubscribe> {
    public static final CloudEventSubscribeBeanBuilder instance = new CloudEventSubscribeBeanBuilder();

    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, CloudEventSubscribe anno) throws Exception {
        if (CloudClient.event() == null) {
            throw new IllegalArgumentException("Missing CloudEventService component");
        }

        if (bw.raw() instanceof CloudEventEntityHandler) {

            Class<?>[] ets = GenericUtil.resolveTypeArguments(clz, CloudEventEntityHandler.class);

            if (ets != null && ets.length > 0) {
                Class<?> entityClz = ets[0];
                CloudEvent anno2 = entityClz.getAnnotation(CloudEvent.class);

                if (anno2 == null) {
                    throw new IllegalArgumentException("The entity is missing (@CloudEvent) annotations: " + this.getClass().getName());
                }

                //支持${xxx}配置
                String topic = Solon.cfg().getByParse(Utils.annoAlias(anno2.value(), anno2.topic()));
                //支持${xxx}配置
                String group = Solon.cfg().getByParse(anno2.group());

                CloudEventHandlerProxy hadnler = new CloudEventHandlerProxy(bw.raw(), entityClz);

                CloudManager.register(anno2, hadnler);
                CloudClient.event().attention(anno2.level(), anno2.channel(), group, topic, bw.raw());
            }
        }
    }
}
