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
package org.noear.solon.net.socketd;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.listener.PipelineListener;
import org.noear.solon.Solon;
import org.noear.solon.net.socketd.listener.PathListenerPlus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * WebSoskcet 路由器
 *
 * @author noear
 * @since 2.6
 */
public class SocketdRouter {
    private final PipelineListener rootListener = new PipelineListener();
    private final PathListenerPlus pathListener = new PathListenerPlus(true);
    private final Set<String> paths = new HashSet<>();

    private SocketdRouter() {
        rootListener.next(pathListener);
    }

    public static SocketdRouter getInstance() {
        //方便在单测环境下切换 SolonApp，可以相互独立
        return Solon.context().attachOf(SocketdRouter.class, SocketdRouter::new);
    }


    /**
     * 前置监听
     */
    public void before(Listener listener) {
        rootListener.prev(listener);
    }

    /**
     * 主监听
     */
    public void of(String path, Listener listener) {
        pathListener.doOf(path, listener);
        paths.add(path);
    }

    /**
     * 后置监听
     */
    public void after(Listener listener) {
        rootListener.next(listener);
    }

    public Listener getListener() {
        return rootListener;
    }

    public Collection<String> getPaths() {
        return Collections.unmodifiableSet(paths);
    }
}
