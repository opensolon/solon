package org.noear.solon.core;

import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.event.EventListener;

import java.lang.reflect.Constructor;

/**
 * 应用上下文（ 为全局对象；热插拨的插件，会产生独立的上下文）
 *
 * 主要实现四个动作：
 * 1.bean 构建
 * 2.bean 注入（字段 或 参数）
 * 3.bean 提取
 * 4.bean 拦截
 *
 * @author noear
 * @since 2.5
 * */
public class AppContext extends AopContext { //（继承，为兼容性过度）
    public AppContext(ClassLoader classLoader, Props props) {
        super(classLoader, props);
    }


    /**
     * 订阅事件
     */
    public <T> AppContext onEvent(Class<T> type, EventListener<T> handler) {
        EventBus.subscribe(type, handler);
        return this;
    }

    /**
     * 订阅事件
     */
    public <T> AppContext onEvent(Class<T> type, int index, EventListener<T> handler) {
        EventBus.subscribe(type, index, handler);
        return this;
    }

    /**
     * 构造实例
     * */
    public  <T> T newInstance(Class<?> clz) throws Exception {
        Constructor c = clz.getDeclaredConstructors()[0];
        if (c.getParameterCount() > 0) {
            Object[] args = new Object[c.getParameterCount()];

            for (int i = 0; i < args.length; i++) {
                args[i] = getBean(c.getParameterTypes()[i]);
            }

            return (T) c.newInstance(args);
        } else {
            return (T) c.newInstance();
        }
    }
}
