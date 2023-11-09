package org.noear.solon.net.websocket;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 2.6
 */
public abstract class WebSocketBase implements WebSocket {

    private final Map<String, Object> attrMap = new HashMap<>();
    private Object attachment;

    @Override
    public <T> void setAttachment(T attachment) {
        this.attachment = attachment;
    }

    @Override
    public <T> T getAttachment() {
        return (T)attachment;
    }


    @Override
    public Map<String, Object> getAttrMap() {
        return attrMap;
    }

    @Override
    public <T> T getAttr(String name) {
        return (T)attrMap.get(name);
    }

    @Override
    public <T> T getAttrOrDefault(String name, T def) {
        return (T) attrMap.getOrDefault(name, def);
    }

    @Override
    public <T> void setAttr(String name, T value) {
        attrMap.put(name,value);
    }
}
