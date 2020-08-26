package org.noear.solon.core;

public class XSessionStateDefault implements XSessionState {
    @Override
    public String sessionId() {
        return null;
    }

    @Override
    public Object sessionGet(String key) {
        return null;
    }

    @Override
    public void sessionSet(String key, Object val) {

    }
}
