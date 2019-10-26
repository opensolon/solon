package org.noear.solon.extend.redissessionstate;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XHandlerLink;
import org.noear.solon.core.XMethod;
import org.noear.solon.core.XPlugin;
import org.noear.solon.core.XSessionStateDefault;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        SessionState sessionState = new SessionState();

        XSessionStateDefault.global = sessionState;

        app.before("*", XMethod.HTTP,(c)->{
            sessionState.updateSessionID();
        });

        System.out.println("solon:: Redis session state is loaded");
    }
}
