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
package org.noear.solon.web.servlet;

import org.noear.solon.core.handle.SessionState;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Servlet，适配为 SessionState 接口
 *
 * @author noear
 * @since 1.6
 */
public class SolonServletSessionState implements SessionState {
    private HttpServletRequest _request;
    public SolonServletSessionState(HttpServletRequest request){
        _request = request;
    }

    @Override
    public long creationTime() {
        return 0;
    }

    @Override
    public long lastAccessTime() {
        return 0;
    }

    @Override
    public String sessionId() {
        return _request.getRequestedSessionId();
    }

    @Override
    public String sessionChangeId() {
        return _request.changeSessionId();
    }

    @Override
    public Collection<String> sessionKeys() {
        return Collections.list(_request.getSession().getAttributeNames());
    }

    @Override
    public <T> T sessionGet(String key, Class<T> clz) {
        return (T) _request.getSession().getAttribute(key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        if (val == null) {
            sessionRemove(key);
        } else {
            _request.getSession().setAttribute(key, val);
        }
    }

    @Override
    public void sessionRemove(String key) {
        _request.getSession().removeAttribute(key);
    }

    @Override
    public void sessionClear() {
        Enumeration<String> names = _request.getSession().getAttributeNames();
        while (names.hasMoreElements()) {
            _request.getSession().removeAttribute(names.nextElement());
        }
    }

    @Override
    public void sessionReset() {
        _request.getSession().invalidate();
    }
}
