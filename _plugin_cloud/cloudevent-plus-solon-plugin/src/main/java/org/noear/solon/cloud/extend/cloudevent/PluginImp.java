package org.noear.solon.cloud.extend.cloudevent;

import org.noear.snack.ONode;
import org.noear.solon.SolonApp;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class PluginImp implements Plugin {

    private final Map<String, Map<String, Set<AbstractMap.SimpleEntry<Object, Method>>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void start(SolonApp app) {
        app.onEvent(AppLoadEndEvent.class, appLoadEndEvent -> {
            Aop.beanForeach(wrap -> {
                Object bean = wrap.get();

                // 获取所有标注有 CloudEventSubscriber 的类
                CloudEventSubscriber typeSubscriber = wrap.annotationGet(CloudEventSubscriber.class);
                if (typeSubscriber != null) {
                    for(Method method : bean.getClass().getDeclaredMethods()) {
                        // 获取所有标注有 Subscribe 的方法
                        CloudEventSubscriber methodSubscriber = method.getAnnotation(CloudEventSubscriber.class);
                        if (methodSubscriber != null) {
                            Class<?>[] types = method.getParameterTypes();
                            // 如果它是标准的 EventBus Subscriber (只有一个参数)
                            if (types.length == 1) {
                                Class<?> eventType = types[0];
                                // 生成注册信息
                                String channel = methodSubscriber.channel().isEmpty() ? typeSubscriber.channel() : methodSubscriber.channel();
                                String group = methodSubscriber.group().isEmpty() ? typeSubscriber.group() : methodSubscriber.group();
                                // 添加到事件管理器中去中去
                                this.register(channel, group, eventType.getName(), new AbstractMap.SimpleEntry<>(
                                        bean, method
                                ));
                            }
                        }
                    }
                }

                // 获取所有 implements CloudEventHandler 的类
                for (Type item : bean.getClass().getGenericInterfaces()) {
                    if (item instanceof ParameterizedType) {
                        ParameterizedType type = (ParameterizedType) item;
                        Class<?> rawType = (Class<?>) type.getRawType();

                        if (rawType == CloudEventHandler.class) {
                            Type[] types = type.getActualTypeArguments();
                            if (types.length == 1) {
                                try {
                                    // Todo: 配置信息注入
                                    this.register("", "", "", new AbstractMap.SimpleEntry<>(
                                            bean, bean.getClass().getMethod("handle", CloudEventEntity.class)
                                    ));
                                } catch (NoSuchMethodException e) {
                                    // Todo: 异常处理
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                }

                this.subscribe();
            });
        });
    }

    public void register(String channel, String group, String topic, AbstractMap.SimpleEntry<Object, Method> handler) {
        Map<String, Set<AbstractMap.SimpleEntry<Object, Method>>> subscribers = this.subscribers.computeIfAbsent(channel + "§" + group, k -> new ConcurrentHashMap<>(1));
        Set<AbstractMap.SimpleEntry<Object, Method>> realMethod = subscribers.computeIfAbsent(topic, k -> new HashSet<>());
        realMethod.add(handler);
    }

    private void subscribe() {
        // 注册到 CloudEventService
        this.subscribers.forEach((k, v) -> {
            String[] info = k.split("§");
            v.forEach((topic, handlers) -> {
                CloudClient.event().attention(EventLevel.cluster, info[0], info[1], topic, event -> {
                    // 要求必须要带有类型
                    Object entity = ONode.deserialize(event.content());
                    handlers.forEach(handler -> {
                        try {
                            handler.getValue().invoke(handler.getKey(), entity);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    boolean isSuccess = ((CloudEventEntity) entity).isSuccess();
                    CloudEventEntity.SUCCESS.remove();
                    return isSuccess;
                });
            });
        });
    }
}
