package org.noear.solon.core;

import org.noear.solon.XApp;

public class XBridge {
    //
    // SessionState 对接
    //
    private static XSessionState sessionState = new XSessionStateDefault();
    private static boolean sessionStateUpdated;

    public static void setSessionState(XSessionState ss) {
        if (ss != null) {
            sessionState = ss;

            if (sessionStateUpdated == false) {
                sessionStateUpdated = true;

                XApp.global().before("**", XMethod.HTTP, (c) -> {
                    sessionState.sessionRefresh();
                });
            }
        }
    }

    public static XSessionState getSessionState() {
        return sessionState;
    }

    static class XSessionStateDefault implements XSessionState {
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


    //
    // UpstreamFactory 对接
    //
    private static XUpstreamFactory upstreamFactory = (service -> {
        throw new RuntimeException("Uninitialized XUpstreamFactory");
    });

    public static XUpstreamFactory getUpstreamFactory() {
        return upstreamFactory;
    }

    public static void setUpstreamFactory(XUpstreamFactory uf) {
        upstreamFactory = uf;
    }
}
