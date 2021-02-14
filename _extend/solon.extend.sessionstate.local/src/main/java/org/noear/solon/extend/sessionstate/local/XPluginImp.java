package org.noear.solon.extend.sessionstate.local;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Solon.global().enableSessionState() == false) {
            return;
        }

        if (Bridge.sessionStateFactory().priority() >= LocalSessionStateFactory.SESSION_STATE_PRIORITY) {
            return;
        }

        XServerProp.init();

        Bridge.sessionStateFactorySet(LocalSessionStateFactory.getInstance());

        System.out.println("solon:: Local session state plugin is loaded");
    }
}
