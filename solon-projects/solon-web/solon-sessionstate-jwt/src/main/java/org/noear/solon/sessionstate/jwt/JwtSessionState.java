/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.sessionstate.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.noear.solon.Utils;
import org.noear.solon.boot.web.SessionStateBase;
import org.noear.solon.core.handle.Context;

import java.util.Collection;
import java.util.ServiceConfigurationError;

/**
 * @author noear
 * @since 1.3
 */
public class JwtSessionState extends SessionStateBase {

    protected JwtSessionState(Context ctx) {
        super(ctx);
    }

    //
    // session control
    //

    @Override
    public String sessionId() {
        if (SessionProp.session_jwt_allowUseHeader) {
            return "";
        }

        return sessionIdGet(false);
    }

    @Override
    public String sessionChangeId() {
        return sessionIdGet(true);
    }

    @Override
    public Collection<String> sessionKeys() {
        return sessionMap.keySet();
    }

    private Claims sessionMap;

    protected Claims sessionMap() {
        if (sessionMap == null) {
            Utils.locker().lock();

            try {
                if (sessionMap == null) {
                    sessionMap = new DefaultClaims(); //先初始化一下，避免异常时进入死循环

                    String sesId = sessionId();
                    String token = jwtGet();

                    if (Utils.isNotEmpty(token) && token.contains(".")) {
                        Claims claims = JwtUtils.parseJwt(token);

                        if (claims != null) {
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
            } finally {
                Utils.locker().unlock();
            }
        }

        return sessionMap;
    }


    @Override
    public <T> T sessionGet(String key, Class<T> clz) {
        return (T) sessionMap().get(key);
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

        sessionIdPush();
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
