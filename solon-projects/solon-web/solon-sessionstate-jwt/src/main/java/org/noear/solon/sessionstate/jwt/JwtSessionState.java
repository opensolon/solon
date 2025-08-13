/*
 * Copyright 2017-2025 noear.org and authors
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
import org.noear.solon.server.ServerConstants;
import org.noear.solon.server.handle.SessionStateBase;
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


    @Override
    public boolean replaceable() {
        return false;
    }

    @Override
    public long creationTime() {
        return ctx.sessionAsLong(ServerConstants.SESSION_CREATION_TIME, 0L);
    }

    @Override
    public long lastAccessTime() {
        return ctx.sessionAsLong(ServerConstants.SESSION_LAST_ACCESS_TIME, 0L);
    }

    //
    // session control
    //
    private String sessionId;

    @Override
    public String sessionId() {
        if (JwtSessionProps.getInstance().allowUseHeader) {
            return "";
        }

        if (sessionId == null) {
            sessionId = sessionIdGet(false);
        }

        return sessionId;
    }

    @Override
    public String sessionChangeId() {
        if (JwtSessionProps.getInstance().allowUseHeader) {
            return "";
        }

        return sessionId = sessionIdGet(true);
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
                            if (JwtSessionProps.getInstance().allowUseHeader || sesId.equals(claims.getId())) {
                                if (JwtSessionProps.getInstance().allowExpire) {
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
        if (JwtSessionProps.getInstance().allowUseHeader) {
            return;
        }

        String sid = sessionIdPush();

        if (Utils.isEmpty(sid) == false) {
            long now = System.currentTimeMillis();

            if (sessionGet(ServerConstants.SESSION_CREATION_TIME) == null) {
                sessionSet(ServerConstants.SESSION_CREATION_TIME, now);
            }
        }
    }

    @Override
    public void sessionPublish() {
        if (JwtSessionProps.getInstance().allowAutoIssue) {
            String sid = sessionId();

            if (Utils.isEmpty(sid) == false) {
                long now = System.currentTimeMillis();

                sessionSet(ServerConstants.SESSION_LAST_ACCESS_TIME, now);
            }

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
                if (JwtSessionProps.getInstance().allowUseHeader && tmp.size() == 0) {
                    sessionToken = "";
                }

                if (sessionToken == null) {
                    String skey = sessionId();

                    if (JwtSessionProps.getInstance().allowUseHeader || Utils.isNotEmpty(skey)) {
                        tmp.setId(skey);

                        try {
                            if (JwtSessionProps.getInstance().allowExpire) {
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


    protected String jwtGet() {
        if (JwtSessionProps.getInstance().allowUseHeader) {
            return ctx.header(JwtSessionProps.getInstance().name);
        } else {
            return cookieGet(JwtSessionProps.getInstance().name);
        }
    }

    protected void jwtSet(String token) {
        if (JwtSessionProps.getInstance().allowUseHeader) {
            ctx.headerSet(JwtSessionProps.getInstance().name, token);
        } else {
            cookieSet(JwtSessionProps.getInstance().name, token);
        }

        ctx.attrSet(JwtSessionProps.getInstance().name, token);
    }
}
