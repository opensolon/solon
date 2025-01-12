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
package org.noear.solon.net.socketd.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.transport.core.listener.PathListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 路径监听器增强版
 *
 * @author noear
 * @since 2.6
 */
public class PathListenerPlus extends PathListener {
    static final Logger log = LoggerFactory.getLogger(PathListenerPlus.class);

    private final boolean autoClose;

    public PathListenerPlus() {
        this(false);
    }

    public PathListenerPlus(boolean autoClose) {
        super(new RouteSelectorExpress());
        this.autoClose = autoClose;
    }

    @Override
    public void onOpen(Session session) throws IOException {
        Listener l1 = this.pathRouteSelector.select(session.path());
        if (l1 != null) {
            l1.onOpen(session);
        } else if (autoClose) {
            session.close();
            log.warn("Route failed. The connection will close. path={}", session.path());
        }
    }

    /**
     * 路由
     */
    @Override
    public PathListener doOf(String path, Listener listener) {
        pathRouteSelector.put(path, new ExpressListener(path, listener));
        return this;
    }

    @Override
    public EventListener of(String path) {
        EventListener listener = new EventListener();
        pathRouteSelector.put(path, new ExpressListener(path, listener));
        return listener;
    }
}