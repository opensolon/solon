package org.noear.solon.extend.sessionstate.redis;

import org.noear.solon.XApp;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        if (XBridge.sessionState() != null
                && XBridge.sessionState().priority() >= RedisSessionState.SESSION_STATE_PRIORITY) {
            return;
        }
        /*
        *
        * server.session.state.redis:
        * server:
        * password:
        * db: 31
        * maxTotaol: 200
        *
        * */
        XServerProp.init();
        RedisSessionState sessionState = RedisSessionState.create();

        if(sessionState != null){
            XBridge.sessionStateSet(sessionState);
        }

        System.out.println("solon:: Redis session state plugin is loaded");
    }
}
