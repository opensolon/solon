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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 事件监听管道
 *
 * @author noear
 * @since 2.4
 */
public class EventListenPipeline<Event> implements EventListener<Event> {
    private List<EH> list = new ArrayList<>();

    /**
     * 添加监听
     */
    public void add(EventListener<Event> listener) {
        add(0, listener);
    }

    /**
     * 添加监听（带顺序位）
     *
     * @param index    顺序位
     * @param listener 监听器
     */
    public void add(int index, EventListener<Event> listener) {
        list.add(new EH(index, listener));
        list.sort(Comparator.comparing(EH::getIndex));
    }

    /**
     * 移除监听
     *
     * @param listener 监听器
     */
    public void remove(EventListener<Event> listener) {
        for (int i = 0; i < list.size(); i++) {
            if (listener.equals(list.get(i).listener)) {
                list.remove(i);
                i--;
            }
        }
    }

    @Override
    public void onEvent(Event event) throws Throwable {
        //用 i，可以避免遍历时添加监听的异常
        for (int i = 0; i < list.size(); i++) {
            list.get(i).listener.onEvent(event);
        }
    }

    static class EH<Event> {
        int index;
        EventListener<Event> listener;

        EH(int index, EventListener<Event> listener) {
            this.index = index;
            this.listener = listener;
        }

        public int getIndex() {
            return index;
        }
    }
}
