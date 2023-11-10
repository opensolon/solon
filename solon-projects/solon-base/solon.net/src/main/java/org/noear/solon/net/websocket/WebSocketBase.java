package org.noear.solon.net.websocket;

import org.noear.solon.Utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * WebSocket 会话接口基础
 *
 * @author noear
 * @since 2.6
 */
public abstract class WebSocketBase implements WebSocket {

    private final Map<String, Object> attrMap = new HashMap<>();
    private final String sid = Utils.guid();
    private Object attachment;
    private Handshake handshake;
    private boolean isClosed;
    private String pathNew;

    protected void init(URI uri) {
        this.handshake = new HandshakeImpl(uri);
    }

    @Override
    public String getSid() {
        return sid;
    }

    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public Handshake getHandshake() {
        return handshake;
    }

    @Override
    public String getParam(String name) {
        return handshake.getParam(name);
    }

    @Override
    public String getParamOrDefault(String name, String def) {
        return handshake.getParamOrDefault(name, def);
    }

    @Override
    public String getPath() {
        if (pathNew == null) {
            return handshake.getPath();
        } else {
            return pathNew;
        }
    }

    @Override
    public void setPathNew(String pathNew) {
        this.pathNew = pathNew;
    }

    @Override
    public <T> void setAttachment(T attachment) {
        this.attachment = attachment;
    }

    @Override
    public <T> T getAttachment() {
        return (T) attachment;
    }


    @Override
    public Map<String, Object> getAttrMap() {
        return attrMap;
    }

    @Override
    public <T> T getAttr(String name) {
        return (T) attrMap.get(name);
    }

    @Override
    public <T> T getAttrOrDefault(String name, T def) {
        return (T) attrMap.getOrDefault(name, def);
    }

    @Override
    public <T> void setAttr(String name, T value) {
        attrMap.put(name, value);
    }

    @Override
    public void close() {
        isClosed = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebSocket)) return false;
        WebSocket that = (WebSocket) o;
        return Objects.equals(getSid(), that.getSid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(sid);
    }
}
