package org.noear.solon.extend.sessionstate.local;

import org.noear.solon.Utils;
import org.noear.solon.boot.web.WebSessionStateBase;
import org.noear.solon.core.handle.Context;

import java.util.Collection;


/**
 * 它会是个单例，不能有上下文数据
 * */
public class LocalSessionState extends WebSessionStateBase {
    private static int _expiry = 60 * 60 * 2;
    private static String _domain = null;
    private static ScheduledStore _store;

    static {
        if (SessionProp.session_timeout > 0) {
            _expiry = SessionProp.session_timeout;
        }

        if (SessionProp.session_state_domain != null) {
            _domain = SessionProp.session_state_domain;
        }

        _store = new ScheduledStore(_expiry);
    }

    private Context ctx;

    protected LocalSessionState(Context ctx) {
        this.ctx = ctx;
    }

    //
    // cookies control

    @Override
    protected String cookieGet(String key) {
        return ctx.cookie(key);
    }
    @Override
    protected void cookieSet(String key, String val) {

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
    // session control
    //

    @Override
    public String sessionId() {
        String _sessionId = ctx.attr("sessionId", null);

        if (_sessionId == null) {
            _sessionId = sessionIdGet(false);
            ctx.attrSet("sessionId", _sessionId);
        }

        return _sessionId;
    }

    @Override
    public String sessionChangeId() {
        sessionIdGet(true);
        ctx.attrSet("sessionId", null);
        return sessionId();
    }

    @Override
    public Collection<String> sessionKeys() {
        return _store.keys();
    }


    @Override
    public Object sessionGet(String key) {
        return _store.get(sessionId(), key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        if (val == null) {
            sessionRemove(key);
        } else {
            _store.put(sessionId(), key, val);
        }
    }

    @Override
    public void sessionRemove(String key) {
        _store.remove(sessionId(), key);
    }

    @Override
    public void sessionClear() {
        _store.clear(sessionId());
    }

    @Override
    public void sessionReset() {
        sessionClear();
        sessionChangeId();
    }

    @Override
    public void sessionRefresh() {
        String skey = sessionIdPush();

        if (Utils.isEmpty(skey) == false) {
            _store.delay(sessionId());
        }
    }


    @Override
    public boolean replaceable() {
        return false;
    }
}
