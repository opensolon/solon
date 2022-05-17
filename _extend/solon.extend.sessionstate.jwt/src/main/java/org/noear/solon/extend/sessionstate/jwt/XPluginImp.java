package org.noear.solon.extend.sessionstate.jwt;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.PrintUtil;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Solon.global().enableSessionState() == false) {
            return;
        }

        if (Bridge.sessionStateFactory().priority() >= JwtSessionStateFactory.SESSION_STATE_PRIORITY) {
            return;
        }

        SessionProp.init();

        Bridge.sessionStateFactorySet(JwtSessionStateFactory.getInstance());

        PrintUtil.info("Session","solon: Jwt session state plugin is loaded");
    }
}
