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
package org.noear.solon.core.handle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.0
 */
public class SessionStateEmpty implements SessionState {

    private Map<String, Object> sessionMap = null;

    public Map<String, Object> sessionMap() {
        if (sessionMap == null) {
            sessionMap = new HashMap<>();
        }

        return sessionMap;
    }

    @Override
    public long creationTime() {
        return 0L;
    }

    @Override
    public long lastAccessTime() {
        return 0L;
    }

    @Override
    public String sessionId() {
        return null;
    }

    @Override
    public String sessionChangeId() {
        return null;
    }

    @Override
    public Collection<String> sessionKeys() {
        return sessionMap().keySet();
    }

    @Override
    public <T> T sessionGet(String key, Class<T> clz) {
        return (T) sessionMap().get(key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        if (val == null) {
            sessionRemove(key);
        } else {
            sessionMap().put(key, val);
        }
    }

    @Override
    public void sessionRemove(String key) {
        if (sessionMap != null) {
            sessionMap.remove(key);
        }
    }

    @Override
    public void sessionClear() {
        if (sessionMap != null) {
            sessionMap.clear();
        }
    }

    @Override
    public void sessionReset() {
        sessionClear();
    }
}
