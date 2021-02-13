package org.noear.solon.extend.sessionstate.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;

import java.security.Key;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class JwtSessionState implements SessionState {
    public final static String SESSIONID_KEY = "SOLONID";

    private final static String JWT_TOKEN = "TOKEN";

    private final static String SESSIONID_encrypt = "DHPjbM5QczZ2cysd4gpDbG/4SnuwzWX3sA1i6AXiAbo=";
    private static Key SESSIONID_encrypt_key = null;
    private Key encrypt_key(){
        if(SESSIONID_encrypt_key == null){
            synchronized (this){
                if(SESSIONID_encrypt_key == null){
                    SESSIONID_encrypt_key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SESSIONID_encrypt));
                }
            }
        }

        return SESSIONID_encrypt_key;
    }


    private JwtSessionState() {
        if (XServerProp.session_timeout > 0) {
            _expiry = XServerProp.session_timeout;
        }

        if (XServerProp.session_state_domain != null) {
            _domain = XServerProp.session_state_domain;
        }
    }

    public static JwtSessionState create(){
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

    public  void   cookieSet(String key, String val) {
        Context ctx = Context.current();

        if (XServerProp.session_state_domain_auto) {
            if (_domain != null) {
                if(ctx.uri().getHost().indexOf(_domain) < 0){ //非安全域
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

        if(Utils.isEmpty(skey)){
            skey = Utils.guid();
            cookieSet(SESSIONID_KEY, skey);
        }

        return skey;
    }

    private Map<String,Object> sessionMap;
    public Map<String,Object>  sessionMap() {
        if (sessionMap == null) {
            synchronized (this) {
                if (sessionMap == null) {
                    sessionMap = new LinkedHashMap<>();

                    String jwt = cookieGet(JWT_TOKEN);
                    if (Utils.isNotEmpty(jwt)) {
                        Claims claims = Jwts.parserBuilder()
                                .setSigningKey(encrypt_key())
                                .build()
                                .parseClaimsJws(jwt)
                                .getBody();

                        sessionMap.putAll(claims);
                    }
                }
            }
        }

        return sessionMap;
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

    public static final int SESSION_STATE_PRIORITY = 1;
    @Override
    public int priority() {
        return SESSION_STATE_PRIORITY;
    }
}
