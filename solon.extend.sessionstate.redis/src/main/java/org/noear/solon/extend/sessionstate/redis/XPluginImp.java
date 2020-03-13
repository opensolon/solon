package org.noear.solon.extend.sessionstate.redis;

import org.noear.solon.XApp;
import org.noear.solon.core.XMethod;
import org.noear.solon.core.XPlugin;
import org.noear.solon.core.XSessionStateDefault;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        if (XSessionStateDefault.global != null
                && XSessionStateDefault.global.priority() >= RedisSessionState.SESSION_STATE_PRIORITY) {
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

        if(sessionState == null){
            //说明，没有配置好
            return;
        }

        XSessionStateDefault.setGlobal(sessionState);

        System.out.println("solon:: Redis session state plugin is loaded");
    }
}
