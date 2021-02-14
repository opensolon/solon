package org.noear.solon.extend.sessionstate.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;

/**
 * @author noear
 * @since 1.3
 */
public class JwtSessionState implements SessionState {
    public final static String SESSIONID_KEY = "SOLONID";
    public final static String SESSION_TOKEN = "TOKEN";

    private static int _expiry = 60 * 60 * 2;
    private static String _domain = null;

    static {
        if (XServerProp.session_timeout > 0) {
            _expiry = XServerProp.session_timeout;
        }

        if (XServerProp.session_state_domain != null) {
            _domain = XServerProp.session_state_domain;
        }
    }

    private Context ctx;

    protected JwtSessionState(Context ctx) {
        this.ctx = ctx;
    }

    //
    // cookies control
    //

    public String cookieGet(String key) {
        return ctx.cookie(key);
    }

    public void cookieSet(String key, String val) {
        if (XServerProp.session_state_domain_auto) {
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
            _sessionId = sessionId_get();
            ctx.attrSet("sessionId", _sessionId);
        }

        return _sessionId;
    }

    private String sessionId_get() {
        String skey = cookieGet(SESSIONID_KEY);

        if (Utils.isEmpty(skey)) {
            skey = Utils.guid();
            cookieSet(SESSIONID_KEY, skey);
        }

        return skey;
    }

    private Claims sessionMap;

    public Claims sessionMap() {
        if (sessionMap == null) {
            synchronized (this) {
                if (sessionMap == null) {

                    String token = token_get();

                    if (Utils.isNotEmpty(token)) {
                        sessionMap = JwtUtils.parseJwt(token);

                        //if (sessionId().equals(claims.getId())) {
                        //    sessionMap.putAll(claims);
                        //}
                    }

                    if(sessionMap == null){
                        sessionMap = new DefaultClaims();
                    }
                }
            }
        }

        return sessionMap;
    }

    protected String token_get() {
        return cookieGet(SESSION_TOKEN);
    }

    @Override
    public Object sessionGet(String key) {
        return sessionMap().get(key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        sessionMap().put(key, val);
    }

    @Override
    public void sessionClear() {
        sessionMap().clear();
    }

    @Override
    public void sessionRefresh() {
        String skey = cookieGet(SESSIONID_KEY);

        if (Utils.isEmpty(skey) == false) {
            cookieSet(SESSIONID_KEY, skey);
        }
    }

    @Override
    public void sessionPublish() {
        if (sessionMap != null && ctx != null) {
            String skey = cookieGet(SESSIONID_KEY);
            if (Utils.isEmpty(skey) == false) {
                sessionMap.setIssuer("Solon");
                sessionMap.setId(skey);
                String token = JwtUtils.buildJwt(sessionMap, _expiry * 1000);
                cookieSet(SESSION_TOKEN, token);
            }
        }
    }


    @Override
    public boolean replaceable() {
        return false;
    }
}
