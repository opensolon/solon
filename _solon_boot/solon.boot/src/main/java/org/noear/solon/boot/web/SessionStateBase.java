package org.noear.solon.boot.web;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;

/**
 * 会话状态基类
 *
 * @author noear
 * @since 1.7
 */
public abstract class SessionStateBase implements SessionState {

    protected static int _expiry = 60 * 60 * 2;
    protected static String _domain = null;

    static {
        if (SessionProps.session_timeout > 0) {
            _expiry = SessionProps.session_timeout;
        }

        if (SessionProps.session_cookieDomain != null) {
            _domain = SessionProps.session_cookieDomain;
        }
    }

    protected final Context ctx;

    protected SessionStateBase(Context ctx) {
        this.ctx = ctx;
    }

    //
    // cookies control
    protected String cookieGet(String key) {
        return ctx.cookie(key);
    }

    protected void cookieSet(String key, String val) {
        if (ctx.url() == null) {
            return;
        }

        if (SessionProps.session_cookieDomainAuto) {
            if (_domain != null) {
                if (ctx.uri().getHost().indexOf(_domain) < 0) { //非安全域
                    ctx.cookieSet(key, val, null, _expiry);
                    return;
                }
            }
        }

        ctx.cookieSet(key, val, _domain, _expiry);
    }


    //
    // sessionId control
    protected String sessionIdGet(boolean reset) {
        String sid = cookieGet(SessionProps.session_cookieName);

        if (reset == false) {
            if (Utils.isEmpty(sid) == false && sid.length() > 30) {
                return sid;
            }
        }

        sid = Utils.guid();
        cookieSet(SessionProps.session_cookieName, sid);

        return sid;
    }

    protected String sessionIdPush() {
        String skey = cookieGet(SessionProps.session_cookieName);

        if (Utils.isNotEmpty(skey)) {
            cookieSet(SessionProps.session_cookieName, skey);
        }

        return skey;
    }
}
