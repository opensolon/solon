package org.noear.solon.sessionstate.redisson;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.PrintUtil;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Solon.global().enableSessionState() == false) {
            return;
        }

        if (Bridge.sessionStateFactory().priority() >= RedissonSessionStateFactory.SESSION_STATE_PRIORITY) {
            return;
        }
        /*
         *
         * server.session.state.redis:
         * server:
         * password:
         * db: 31
         * maxTotal: 200
         *
         * */
        SessionProp.init();

        if (RedissonSessionStateFactory.getInstance().redisClient() == null) {
            return;
        }

        Bridge.sessionStateFactorySet(RedissonSessionStateFactory.getInstance());

        PrintUtil.info("Session","solon: Redis session state plugin is loaded");
    }
}
