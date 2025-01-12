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
package demo;

import org.noear.solon.core.handle.SessionState;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

/**
 * @author noear 2022/1/18 created
 */
public class HttpSessionImpl implements HttpSession {
    SessionState sessionState;
    HttpServletRequest request;

    public HttpSessionImpl(HttpServletRequest request, SessionState sessionState) {
        this.sessionState = sessionState;
        this.request = request;
    }

    @Override
    public long getCreationTime() {
        return 0;
    }

    @Override
    public String getId() {
        return sessionState.sessionId();
    }

    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return request.getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int i) {

    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return request.getSession().getSessionContext();
    }

    @Override
    public Object getAttribute(String s) {
        return sessionState.sessionGet(s, Object.class);
    }

    @Override
    public Object getValue(String s) {
        return sessionState.sessionGet(s, Object.class);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String s, Object o) {
        sessionState.sessionSet(s,o);
    }

    @Override
    public void putValue(String s, Object o) {
        sessionState.sessionSet(s,o);
    }

    @Override
    public void removeAttribute(String s) {
        sessionState.sessionRemove(s);
    }

    @Override
    public void removeValue(String s) {
        sessionState.sessionRemove(s);
    }

    @Override
    public void invalidate() {

    }

    @Override
    public boolean isNew() {
        return false;
    }
}
