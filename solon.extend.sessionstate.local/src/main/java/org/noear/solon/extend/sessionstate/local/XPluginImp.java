package org.noear.solon.extend.sessionstate.local;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.solon.core.XSessionStateDefault;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        if (XSessionStateDefault.global != null
                && XSessionStateDefault.global.priority() >= LocalSessionState.SESSION_STATE_PRIORITY) {
            return;
        }

        XServerProp.init();
        LocalSessionState sessionState = LocalSessionState.create();

        XSessionStateDefault.setGlobal(sessionState);

        System.out.println("solon:: Local session state plugin is loaded");
    }
}
