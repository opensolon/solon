/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core.event;

import org.noear.solon.Utils;
import org.noear.solon.core.exception.EventException;
import org.noear.solon.core.util.EgggUtil;
import org.noear.solon.core.util.RunUtil;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.*;

/**
 * 事件总线（内部类，外部不要使用）
 *
 * @see org.noear.solon.core.AppContext#start()
 * @see org.noear.solon.SolonApp#onEvent(Class, EventListener)
 * */
public final class EventBus {
    //异常订阅者
    private static List<HH> sThrow = new ArrayList<>();
    //其它订阅者
    private static List<HH> sOther = new ArrayList<>();
    //订阅管道
    private static Map<Class<?>, EventListenPipeline<?>> sPipeline = new HashMap<>();

    /**
     * 异步推送事件（一般不推荐）；
     *
     * @param event 事件（可以是任何对象）
     */
    public static void publishAsync(Object event) {
        if (event != null) {
            RunUtil.async(() -> {
                try {
                    publish0(event);
                } catch (Throwable e) {
                    publish(e);
                }
            });
        }
    }


    /**
     * 同步推送事件（不抛异常，不具有事务回滚传导性）
     *
     * @param event 事件（可以是任何对象）
     */
    public static void publishTry(Object event) {
        if (event != null) {
            try {
                publish0(event);
            } catch (Throwable e) {
                //不再转发异常，免得死循环
                LoggerFactory.getLogger(EventBus.class).warn("EventBus publishTry failed!", e);
            }
        }
    }


    /**
     * 同步推送事件（会抛异常，可传导事务回滚）
     *
     * @param event 事件（可以是任何对象）
     */
    public static void publish(Object event) throws RuntimeException {
        if (event != null) {
            try {
                publish0(event);
            } catch (Throwable e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new EventException("Event execution failed: " + event.getClass().getName(), e);
                }
            }
        }
    }

    private static void publish0(Object event) throws Throwable {
        if (event instanceof Throwable) {
            //异常分发
            publish1(sThrow, event, false);
        } else {
            //其它事件分发
            publish1(sOther, event, true);
        }
    }

    private static void publish1(List<HH> hhs, Object event, boolean thrown) throws Throwable {
        //用 i，可以避免遍历时添加监听的异常
        for (int i = 0; i < hhs.size(); i++) {
            HH h1 = hhs.get(i);
            if (h1.t.isInstance(event)) {
                try {
                    h1.l.onEvent(event);
                } catch (Throwable e) {
                    if (thrown) {
                        throw e;
                    } else {
                        //此处不能再转发异常 //不然会死循环
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 订阅事件
     *
     * @param eventType 事件类型
     * @param listener  事件监听者
     */
    public static <T> void subscribe(Class<T> eventType, EventListener<T> listener) {
        Utils.locker().lock();

        try {
            pipelineDo(eventType).add(listener);
        } finally {
            Utils.locker().unlock();
        }
    }

    /**
     * 订阅事件
     *
     * @param eventType 事件类型
     * @param index     顺序位
     * @param listener  事件监听者
     */
    public static <T> void subscribe(Class<T> eventType, int index, EventListener<T> listener) {
        Utils.locker().lock();

        try {
            pipelineDo(eventType).add(index, listener);
        } finally {
            Utils.locker().unlock();
        }
    }

    /**
     * 建立订阅管道
     *
     * @param eventType 事件类型
     */
    private static <T> EventListenPipeline<T> pipelineDo(Class<T> eventType) {
        EventListenPipeline<T> pipeline = (EventListenPipeline<T>) sPipeline.get(eventType);

        if (pipeline == null) {
            pipeline = new EventListenPipeline<>();
            sPipeline.put(eventType, pipeline);
            registerDo(eventType, pipeline);
        }

        return pipeline;
    }

    private static <T> void registerDo(Class<T> eventType, EventListener<T> listener) {
        if (Throwable.class.isAssignableFrom(eventType)) {
            sThrow.add(new HH(eventType, listener));
        } else {
            sOther.add(new HH(eventType, listener));
        }
    }

    /**
     * 取消事件订阅
     *
     * @param listener 事件监听者
     */
    public static <T> void unsubscribe(EventListener<T> listener) {
        Utils.locker().lock();

        try {
            Type tType = EgggUtil.findGenericInfo(listener.getClass(), EventListener.class).get("T");
            if (tType instanceof Class<?>) {
                pipelineDo((Class<T>) tType).remove(listener);
            }
        } finally {
            Utils.locker().unlock();
        }
    }

    /**
     * Listener Holder
     */
    static class HH {
        protected Class<?> t;
        protected EventListener l;

        public HH(Class<?> type, EventListener listener) {
            this.t = type;
            this.l = listener;
        }
    }
}
