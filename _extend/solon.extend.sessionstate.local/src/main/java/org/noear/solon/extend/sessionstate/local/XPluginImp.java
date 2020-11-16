package org.noear.solon.extend.sessionstate.local;

import org.noear.solon.Solon;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(Solon app) {
        if(Solon.global().enableSessionState() == false){
            return;
        }

        if (Bridge.sessionState() != null
                && Bridge.sessionState().priority() >= LocalSessionState.SESSION_STATE_PRIORITY) {
            return;
        }

        XServerProp.init();
        LocalSessionState sessionState = LocalSessionState.create();

        if (sessionState != null) {
            Bridge.sessionStateSet(sessionState);
        }

        System.out.println("solon:: Local session state plugin is loaded");
    }
}
