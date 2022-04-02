package org.noear.solon.cloud.impl;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudEventInterceptor;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventServicePlus;
import org.noear.solon.core.Aop;

import java.util.HashMap;
import java.util.Map;


/**
 * 事件服务管理器及代理实现（用以支持多通道）
 *
 * @author noear
 * @since 1.5
 */
public class CloudEventServiceManagerImpl implements CloudEventServiceManager {
    Map<String, CloudEventServicePlus> route = new HashMap<>();
    CloudEventInterceptor eventInterceptor;

    public CloudEventServiceManagerImpl() {
        Aop.getAsyn(CloudEventInterceptor.class, bw -> {
            eventInterceptor = bw.raw();
        });
    }

    /**
     * 获取拦截器
     * */
    public CloudEventInterceptor getEventInterceptor() {
        return eventInterceptor;
    }

    /**
     * 注册事件服务
     *
     * @param service 事件服务
     */
    public void register(CloudEventServicePlus service) {
        if (service.getChannel() == null) {
            route.put("", service);
        } else {
            route.put(service.getChannel(), service);
        }
    }

    /**
     * 获取事件服务
     *
     * @param channel 通道
     */
    public CloudEventServicePlus get(String channel) {
        if (channel == null) {
            channel = "";
        }

        return route.get(channel);
    }

    /**
     * 获取事件服务，如果没有则异常
     *
     * @param channel 通道
     */
    public CloudEventServicePlus getOrThrow(String channel) {
        CloudEventServicePlus tmp = get(channel);

        if (tmp == null) {
            if (Utils.isEmpty(channel)) {
                throw new RuntimeException("CloudEventService does not exist");
            } else {
                throw new RuntimeException("CloudEventService does not exist channel &" + channel);
            }
        }

        return tmp;
    }

    /**
     * 发布事件
     *
     * @param event 事件
     */
    @Override
    public boolean publish(Event event) throws CloudEventException {
        CloudEventServicePlus tmp = getOrThrow(event.channel());

        if (Utils.isEmpty(event.group())) {
            event.group(tmp.getGroup());
        }

        return tmp.publish(event);
    }

    /**
     * 关注事件
     *
     * @param level    级别
     * @param channel  通道
     * @param group    分组
     * @param topic    主题
     * @param observer 观察者
     */
    @Override
    public void attention(EventLevel level, String channel, String group, String topic, CloudEventHandler observer) {
        CloudEventServicePlus tmp = getOrThrow(channel);

        if (Utils.isEmpty(group)) {
            group = tmp.getGroup();
        }

        tmp.attention(level, channel, group, topic, observer);
    }
}
