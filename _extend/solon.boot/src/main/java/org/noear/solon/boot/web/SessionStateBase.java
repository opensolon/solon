package org.noear.solon.boot.web;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.core.handle.SessionState;

/**
 * @author noear
 * @since 1.7
 */
public abstract class SessionStateBase implements SessionState {
    protected abstract String cookieGet(String key);

    protected abstract void cookieSet(String key, String val);

    protected String sessionIdGet(boolean reset) {
        String skey = cookieGet(ServerConstants.SESSIONID_KEY);

        if (reset == false) {
            if (Utils.isEmpty(skey) == false && skey.length() > 30) {
                return skey;
            }
        }

        skey = Utils.guid();
        cookieSet(ServerConstants.SESSIONID_KEY, skey);
        return skey;
    }

    protected String sessionIdPush() {
        String skey = cookieGet(ServerConstants.SESSIONID_KEY);

        if (Utils.isNotEmpty(skey)) {
            cookieSet(ServerConstants.SESSIONID_KEY, skey);
        }

        return skey;
    }
}
