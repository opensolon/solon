/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.sessionstate.local;

import org.noear.solon.Utils;
import org.noear.solon.boot.web.SessionStateBase;
import org.noear.solon.core.handle.Context;

import java.util.Collection;


/**
 * 它会是个单例，不能有上下文数据
 * */
public class LocalSessionState extends SessionStateBase {

    private static ScheduledStore _store;

    static {
        _store = new ScheduledStore(_expiry);
    }

    protected LocalSessionState(Context ctx) {
        super(ctx);
    }


    @Override
    public boolean replaceable() {
        return false;
    }

    @Override
    public long creationTime() {
        return _store.creationTime(sessionId());
    }

    @Override
    public long lastAccessTime() {
        return _store.lastAccessTime(sessionId());
    }


    //
    // session control
    //

    private String sessionId;
    @Override
    public String sessionId() {
        if (sessionId == null) {
            sessionId = sessionIdGet(false);
        }
        return sessionId;
    }

    @Override
    public String sessionChangeId() {
        return sessionId = sessionIdGet(true);
    }

    @Override
    public Collection<String> sessionKeys() {
        return _store.getKeys(sessionId());
    }

    @Override
    public <T> T sessionGet(String key, Class<T> clz) {
        return (T) _store.get(sessionId(), key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        if (val == null) {
            sessionRemove(key);
        } else {
            _store.put(sessionId(), key, val);
        }
    }

    @Override
    public void sessionRemove(String key) {
        _store.remove(sessionId(), key);
    }

    @Override
    public void sessionClear() {
        _store.clear(sessionId());
    }

    @Override
    public void sessionReset() {
        sessionClear();
        sessionChangeId();
    }

    @Override
    public void sessionRefresh() {
        String sid = sessionIdPush();

        if (Utils.isEmpty(sid) == false) {
            _store.updateAccessedTime(sessionId());
        }
    }
}