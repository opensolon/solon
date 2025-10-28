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

/**
 * 事件监听者
 *
 * <pre>{@code
 * //demo1: 在手动添加Listener
 * Solon.app().onEvent(Throwable.class, (err)->{
 *
 * });
 *
 * //demo2: 自动添加Listener
 * @Component
 * public class ThrowableListener implements EventListener<Throwable>{
 *     public void onEvent(Throwable err){
 *
 *     }
 * }
 *
 * //demo3: 自定义事件及监听（事件可以是任何对象）
 * //订阅事件
 * Solon.app().onEvent(ComAdaptor.class,(oc)->{ ... });
 *
 * //发布事件
 * EventBus.publish(new ComAdaptor());
 * }</pre>
 * @author noear
 * @since 1.0
 * */
public interface EventListener<T> {
    void onEvent(T event) throws Throwable;
}
