package org.noear.solon.extend.sessionstate.local;

import org.noear.solon.XApp;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        if(XApp.global().enableSessionState() == false){
            return;
        }

        if (XBridge.sessionState() != null
                && XBridge.sessionState().priority() >= LocalSessionState.SESSION_STATE_PRIORITY) {
            return;
        }

        XServerProp.init();
        LocalSessionState sessionState = LocalSessionState.create();

        if (sessionState != null) {
            XBridge.sessionStateSet(sessionState);
        }

        System.out.println("solon:: Local session state plugin is loaded");
    }
}
