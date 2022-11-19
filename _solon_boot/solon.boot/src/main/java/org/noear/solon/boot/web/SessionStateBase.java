package org.noear.solon.boot.web;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.util.LogUtil;

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
        if (SessionProp.session_timeout > 0) {
            _expiry = SessionProp.session_timeout;
        }

        if (SessionProp.session_state_domain != null) {
            _domain = SessionProp.session_state_domain;
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
        if (ctx.uri() == null) {
            LogUtil.global().warn("The cookie set failed: url=" + ctx.url());
            return;
        }

        if (SessionProp.session_state_domain_auto) {
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
