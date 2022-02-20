package com.fujieid.jap.solon;

import org.noear.solon.core.handle.SessionState;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

/**
 * @author noear
 * @since 1.6
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
        return sessionState.sessionGet(s);
    }

    @Override
    public Object getValue(String s) {
        return sessionState.sessionGet(s);
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
