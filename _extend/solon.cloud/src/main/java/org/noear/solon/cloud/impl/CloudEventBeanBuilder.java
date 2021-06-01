package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;

/**
 * @author noear
 * @since 1.4
 */
public class CloudEventBeanBuilder implements BeanBuilder<CloudEvent> {
    public static final CloudEventBeanBuilder instance = new CloudEventBeanBuilder();

    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, CloudEvent anno) throws Exception {
        if (bw.raw() instanceof CloudEventHandler) {
            CloudManager.register(anno, bw.raw());

            if (CloudClient.event() != null) {
                String topic = anno.value();
                String group = anno.group();

                //支持${xxx}配置
                if (topic.startsWith("${")) {
                    topic = Solon.cfg().getByExpr(topic);
                }

                //支持${xxx}配置
                if (group.startsWith("${")) {
                    group = Solon.cfg().getByExpr(group);
                }

                //关注事件
                CloudClient.event().attention(anno.level(), anno.channel(), group, topic, bw.raw());
            }
        }
    }
}
