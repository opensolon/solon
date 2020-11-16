package org.noear.solon.extend.sessionstate.redis;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if(Solon.global().enableSessionState() == false){
            return;
        }

        if (Bridge.sessionState() != null
                && Bridge.sessionState().priority() >= RedisSessionState.SESSION_STATE_PRIORITY) {
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
            Bridge.sessionStateSet(sessionState);
        }

        System.out.println("solon:: Redis session state plugin is loaded");
    }
}
