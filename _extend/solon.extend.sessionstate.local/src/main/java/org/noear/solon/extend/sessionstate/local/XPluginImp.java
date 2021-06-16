package org.noear.solon.extend.sessionstate.local;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.PrintUtil;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Solon.global().enableSessionState() == false) {
            return;
        }

        if (Bridge.sessionStateFactory().priority() >= LocalSessionStateFactory.SESSION_STATE_PRIORITY) {
            return;
        }

        SessionProp.init();

        Bridge.sessionStateFactorySet(LocalSessionStateFactory.getInstance());

        PrintUtil.info("Session","solon: Local session state plugin is loaded");
    }
}
