package org.noear.solon.extend.sessionstate.jwt;

import io.jsonwebtoken.Claims;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class JwtSessionState implements SessionState {
    public final static String SESSIONID_KEY = "SOLONID";
    public final static String SESSION_TOKEN = "TOKEN";


    private JwtSessionState() {
        if (XServerProp.session_timeout > 0) {
            _expiry = XServerProp.session_timeout;
        }

        if (XServerProp.session_state_domain != null) {
            _domain = XServerProp.session_state_domain;
        }
    }

    public static JwtSessionState create() {
        return new JwtSessionState();
    }

    //
    // cookies control
    //
    private int _expiry = 60 * 60 * 2;
    private String _domain = null;

    public String cookieGet(String key) {
        return Context.current().cookie(key);
    }

    public void cookieSet(String key, String val) {
        Context ctx = Context.current();

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
    public boolean replaceable() {
        return false;
    }

    @Override
    public String sessionId() {
        String _sessionId = Context.current().attr("sessionId", null);

        if (_sessionId == null) {
            _sessionId = sessionId_get();
            Context.current().attrSet("sessionId", _sessionId);
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

    private Map<String, Object> sessionMap;

    public Map<String, Object> sessionMap() {
        if (sessionMap == null) {
            synchronized (this) {
                if (sessionMap == null) {
                    sessionMap = new LinkedHashMap<>();

                    String token = token_get();
                    if (Utils.isNotEmpty(token)) {
                        Claims claims = JwtUtils.parseJwt(token);

                        if (sessionId().equals(claims.getId())) {
                            sessionMap.putAll(claims);
                        }
                    }
                }
            }
        }

        return sessionMap;
    }

    public String token_get(){
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
        if (sessionMap != null) {
            String token = JwtUtils.buildJwt(sessionMap, _expiry * 1000);
            cookieSet(SESSION_TOKEN, token);
        }
    }

    public static final int SESSION_STATE_PRIORITY = 2;

    @Override
    public int priority() {
        return SESSION_STATE_PRIORITY;
    }
}
