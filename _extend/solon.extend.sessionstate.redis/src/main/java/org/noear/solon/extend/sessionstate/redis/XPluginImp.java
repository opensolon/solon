package org.noear.solon.extend.sessionstate.redis;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.PrintUtil;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Solon.app().enableSessionState() == false) {
            return;
        }

        if (Bridge.sessionStateFactory().priority() >= RedisSessionStateFactory.SESSION_STATE_PRIORITY) {
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

        if (RedisSessionStateFactory.getInstance().redisClient() == null) {
            return;
        }

        Bridge.sessionStateFactorySet(RedisSessionStateFactory.getInstance());

        PrintUtil.info("Session","solon: Redis session state plugin is loaded");
    }
}
