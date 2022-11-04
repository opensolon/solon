package org.noear.solon.sessionstate.jedis;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.LogUtil;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Solon.app().enableSessionState() == false) {
            return;
        }

        if (Bridge.sessionStateFactory().priority() >= JedisSessionStateFactory.SESSION_STATE_PRIORITY) {
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

        if (JedisSessionStateFactory.getInstance().redisClient() == null) {
            return;
        }

        Bridge.sessionStateFactorySet(JedisSessionStateFactory.getInstance());

        LogUtil.info("Session: Redis session state plugin is loaded");
    }
}
