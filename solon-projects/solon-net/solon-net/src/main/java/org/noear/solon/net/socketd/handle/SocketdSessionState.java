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
package org.noear.solon.net.socketd.handle;

import org.noear.socketd.transport.core.Session;
import org.noear.solon.core.handle.SessionState;

import java.util.Collection;

/**
 * Socket.D 会话状态适配
 *
 * @author noear
 * @since 2.0
 */
public class SocketdSessionState implements SessionState {
    Session session;

    public SocketdSessionState(Session session) {
        this.session = session;
    }

    @Override
    public boolean replaceable() {
        return false;
    }

    @Override
    public long creationTime() {
        return 0;
    }

    @Override
    public long lastAccessTime() {
        return session.liveTime();
    }

    @Override
    public String sessionId() {
        return session.sessionId();
    }

    @Override
    public String sessionChangeId() {
        return session.sessionId();
    }

    @Override
    public Collection<String> sessionKeys() {
        return session.attrMap().keySet();
    }

    @Override
    public <T> T sessionGet(String key, Class<T> clz) {
        return (T) session.attr(key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        if (val == null) {
            sessionRemove(key);
        } else {
            session.attrPut(key, val);
        }
    }

    @Override
    public void sessionRemove(String key) {
        session.attrMap().remove(key);
    }

    @Override
    public void sessionClear() {
        session.attrMap().clear();
    }

    @Override
    public void sessionReset() {
        sessionClear();
        //no sup reset id
    }
}
