package org.noear.solon.core;


import org.noear.solon.XApp;

/**
 * 默认Session State 服务（可以通过 global 切换不同的默认服务）
 * */
public class XSessionStateDefault implements XSessionState {
    /**
     * 通过修改 global，可以切换不同的默认 Session State 服务
     * */
    private static boolean globalUpdated= false;
    public static XSessionState global = new XSessionStateDefault();
    public static void setGlobal(XSessionState sessionState) {
        global = sessionState;

        if (global != null && globalUpdated == false) {
            globalUpdated = true;

            XApp.global().before("**", XMethod.HTTP, (c) -> {
                XSessionStateDefault.global.sessionRefresh();
            });
        }
    }

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
