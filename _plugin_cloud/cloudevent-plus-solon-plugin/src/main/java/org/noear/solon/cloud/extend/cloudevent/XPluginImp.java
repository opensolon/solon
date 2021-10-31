package org.noear.solon.cloud.extend.cloudevent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.noear.snack.ONode;
import org.noear.solon.SolonApp;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(SolonApp app) {
        app.onEvent(AppLoadEndEvent.class, appLoadEndEvent -> {
            Aop.beanForeach(wrap -> {
                Object bean = wrap.get();

                // 获取所有标注有 CloudEvent 的类
                CloudEvent cloudEvent = wrap.annotationGet(CloudEvent.class);
                if (cloudEvent != null) {
                    for(Method method : bean.getClass().getDeclaredMethods()) {
                        // 获取所有标注有 Subscribe 的方法
                        if (method.getAnnotation(Subscribe.class) != null) {
                            Class<?>[] types = method.getParameterTypes();
                            // 如果它是标准的 EventBus Subscriber (只有一个参数)
                            if (types.length == 1) {
                                // Todo: 如果它注册的是标准的 CloudEvent (继承了 BaseEvent)
                                Class<?> eventType = types[0];
                                // 将它注册到 CloudEvent 中去
                                CloudClient.event().attention(EventLevel.cluster, cloudEvent.channel(), cloudEvent.group(), eventType.getName(), event -> {
                                    Object event0 = ONode.deserialize(event.content(), eventType);
                                    EventBus.getDefault().post(event0);
                                    return ((CloudEventEntity) event0).isSuccess();
                                });
                            }
                        }
                    }
                }

                for (Type item : bean.getClass().getGenericInterfaces()) {
                    if (item instanceof ParameterizedType) {
                        ParameterizedType type = (ParameterizedType) item;
                        Class<?> rawType = (Class<?>) type.getRawType();

                        if (rawType == CloudEventSubscriber.class) {
                            Type[] types = type.getActualTypeArguments();
                            if (types.length == 1) {
                                CloudClient.event().attention(EventLevel.cluster, cloudEvent.channel(), cloudEvent.group(), types[0].getTypeName(), event -> {
                                    Object event0 = ONode.deserialize(event.content(), Class.forName(types[0].getTypeName()));
                                    EventBus.getDefault().post(event0);
                                    return ((CloudEventEntity) event0).isSuccess();
                                });
                            }
                        }
                    }

                }

            });
        });
    }
}
