package org.noear.solon.core.handle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.6
 */
public class SessionStateEmpty implements SessionState{
    private Map<String,Object> sessionMap = null;
    public Map<String,Object> sessionMap(){
        if(sessionMap == null){
            sessionMap = new HashMap<>();
        }

        return sessionMap;
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
    public Object sessionGet(String key) {
        return sessionMap().get(key);
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
        sessionMap().remove(key);
    }

    @Override
    public void sessionClear() {
        sessionMap().clear();
    }

    @Override
    public void sessionReset() {
        sessionMap().clear();
    }
}
