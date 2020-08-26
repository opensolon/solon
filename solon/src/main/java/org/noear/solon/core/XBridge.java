package org.noear.solon.core;

import org.noear.solon.XApp;

public class XBridge {
    //
    // SessionState 对接
    //
    private static XSessionState _sessionState = new XSessionStateDefault();
    private static boolean _sessionStateUpdated;

    public static void sessionStateSet(XSessionState ss) {
        if (ss != null) {
            _sessionState = ss;

            if (_sessionStateUpdated == false) {
                _sessionStateUpdated = true;

                XApp.global().before("**", XMethod.HTTP, (c) -> {
                    _sessionState.sessionRefresh();
                });
            }
        }
    }

    public static XSessionState sessionState() {
        return _sessionState;
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
    private static XUpstreamFactory _upstreamFactory = (service -> {
        throw new RuntimeException("Uninitialized upstreamFactory");
    });

    public static XUpstreamFactory upstreamFactory() {
        return _upstreamFactory;
    }

    public static void upstreamFactorySet(XUpstreamFactory uf) {
        _upstreamFactory = uf;
    }
}
