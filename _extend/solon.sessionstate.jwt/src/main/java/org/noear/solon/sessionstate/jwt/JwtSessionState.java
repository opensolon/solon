package org.noear.solon.sessionstate.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;

import java.util.Collection;
import java.util.ServiceConfigurationError;

/**
 * @author noear
 * @since 1.3
 */
public class JwtSessionState implements SessionState {
    /**
     * 单位：秒
     * */
    private static int _expiry = 60 * 60 * 2;
    private static String _domain = null;

    static {
        if (SessionProp.session_timeout > 0) {
            _expiry = SessionProp.session_timeout;
        }

        if (SessionProp.session_state_domain != null) {
            _domain = SessionProp.session_state_domain;
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
        if (SessionProp.session_jwt_allowUseHeader) {
            return "";
        }

        String _sessionId = ctx.attr("sessionId", null);

        if (_sessionId == null) {
            _sessionId = sessionId_get(false);
            ctx.attrSet("sessionId", _sessionId);
        }

        return _sessionId;
    }

    @Override
    public String sessionChangeId() {
        sessionId_get(true);
        ctx.attrSet("sessionId", null);
        return sessionId();
    }

    @Override
    public Collection<String> sessionKeys() {
        return sessionMap.keySet();
    }

    private String sessionId_get(boolean reset) {
        String skey = cookieGet(ServerConstants.SESSIONID_KEY);
        String smd5 = cookieGet(ServerConstants.SESSIONID_MD5_KEY);

        if(reset == false) {
            if (Utils.isEmpty(skey) == false && Utils.isEmpty(smd5) == false) {
                if (Utils.md5(skey + ServerConstants.SESSIONID_salt).equals(smd5)) {
                    return skey;
                }
            }
        }

        skey = Utils.guid();
        cookieSet(ServerConstants.SESSIONID_KEY, skey);
        cookieSet(ServerConstants.SESSIONID_MD5_KEY, Utils.md5(skey + ServerConstants.SESSIONID_salt));
        return skey;
    }

    private Claims sessionMap;

    protected Claims sessionMap() {
        if (sessionMap == null) {
            synchronized (this) {
                if (sessionMap == null) {
                    sessionMap = new DefaultClaims(); //先初始化一下，避免异常时进入死循环

                    String sesId = sessionId();
                    String token = jwtGet();

                    if (Utils.isNotEmpty(token) && token.contains(".")) {
                        Claims claims = JwtUtils.parseJwt(token);

                        if(claims != null) {
                            if (SessionProp.session_jwt_allowUseHeader || sesId.equals(claims.getId())) {
                                if (SessionProp.session_jwt_allowExpire) {
                                    if (claims.getExpiration() != null &&
                                            claims.getExpiration().getTime() > System.currentTimeMillis()) {
                                        sessionMap = claims;
                                    }
                                } else {
                                    sessionMap = claims;
                                }
                            }
                        }
                    }

                    sessionToken = null;
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
        if (val == null) {
            sessionRemove(key);
        } else {
            sessionMap().put(key, val);
            sessionToken = null;
        }
    }

    @Override
    public void sessionRemove(String key) {
        sessionMap().remove(key);
        sessionToken = null;
    }

    @Override
    public void sessionClear() {
        sessionMap().clear();
        sessionToken = null;
    }

    @Override
    public void sessionReset() {
        sessionClear();
        sessionChangeId();
    }

    @Override
    public void sessionRefresh() {
        if (SessionProp.session_jwt_allowUseHeader) {
            return;
        }

        String skey = cookieGet(ServerConstants.SESSIONID_KEY);

        if (Utils.isNotEmpty(skey)) {
            cookieSet(ServerConstants.SESSIONID_KEY, skey);
            cookieSet(ServerConstants.SESSIONID_MD5_KEY, Utils.md5(skey + ServerConstants.SESSIONID_salt));
        }
    }

    @Override
    public void sessionPublish() {
        if (SessionProp.session_jwt_allowAutoIssue) {
            String token = sessionToken();

            if (Utils.isNotEmpty(token)) {
                jwtSet(token);
            }
        }
    }

    private String sessionToken;
    @Override
    public String sessionToken() {
        if (sessionToken == null) {
            Claims tmp = sessionMap();

            if (tmp != null) {
                if (SessionProp.session_jwt_allowUseHeader && tmp.size() == 0) {
                    sessionToken = "";
                }

                if (sessionToken == null) {
                    String skey = sessionId();

                    if (SessionProp.session_jwt_allowUseHeader || Utils.isNotEmpty(skey)) {
                        tmp.setId(skey);

                        try {
                            if (SessionProp.session_jwt_allowExpire) {
                                sessionToken = JwtUtils.buildJwt(tmp, _expiry * 1000L);
                            } else {
                                sessionToken = JwtUtils.buildJwt(tmp, 0);
                            }
                        } catch (ServiceConfigurationError e) {
                            //服务切换时，可能配置文件无法加载
                            sessionToken = "";
                        }
                    }
                }
            }
        }

        return sessionToken;
    }

    @Override
    public boolean replaceable() {
        return false;
    }


    protected String jwtGet() {
        if (SessionProp.session_jwt_allowUseHeader) {
            return ctx.header(SessionProp.session_jwt_name);
        } else {
            return cookieGet(SessionProp.session_jwt_name);
        }
    }

    protected void jwtSet(String token) {
        if (SessionProp.session_jwt_allowUseHeader) {
            ctx.headerSet(SessionProp.session_jwt_name, token);
        } else {
            cookieSet(SessionProp.session_jwt_name, token);
        }

        ctx.attrSet(SessionProp.session_jwt_name, token);
    }
}
