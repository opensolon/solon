package org.noear.solon.core;


/**
 * 默认Session State 服务（可以通过 global 切换不同的默认服务）
 * */
public class XSessionStateDefault implements XSessionState {
    /**
     * 通过修改 global，可以切换不同的默认 Session State 服务
     * */
    public static XSessionState global = new XSessionStateDefault();

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
