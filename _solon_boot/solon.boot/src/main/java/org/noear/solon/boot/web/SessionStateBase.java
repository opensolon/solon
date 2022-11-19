package org.noear.solon.boot.web;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.handle.SessionState;

/**
 * 会话状态基类
 *
 * @author noear
 * @since 1.7
 */
public abstract class SessionStateBase implements SessionState {

    protected abstract String cookieGet(String key);

    protected abstract void cookieSet(String key, String val);

    protected String sessionIdGet(boolean reset) {
        String sid = cookieGet(ServerProps.session_cookieName);

        if (reset == false) {
            if (Utils.isEmpty(sid) == false && sid.length() > 30) {
                return sid;
            }
        }

        sid = Utils.guid();
        cookieSet(ServerProps.session_cookieName, sid);
        //监容旧的策略
        cookieSet(ServerProps.session_cookieName2, Utils.md5(sid + ServerConstants.SESSIONID_MD5_SALT));

        return sid;
    }

    protected String sessionIdPush() {
        String skey = cookieGet(ServerProps.session_cookieName);

        if (Utils.isNotEmpty(skey)) {
            cookieSet(ServerProps.session_cookieName, skey);
        }

        return skey;
    }
}
