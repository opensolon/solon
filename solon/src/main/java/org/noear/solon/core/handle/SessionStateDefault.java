package org.noear.solon.core.handle;

/**
 * @author noear 2021/2/14 created
 */
public class SessionStateDefault implements SessionState{
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
