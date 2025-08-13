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
package org.noear.solon.server.handle;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;

/**
 * Web 会话状态基类
 *
 * @author noear
 * @since 1.7
 * @since 3.5
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
        ctx.cookieMap().put(SessionProps.session_cookieName, sid);

        return sid;
    }

    protected String sessionIdPush() {
        String sid = cookieGet(SessionProps.session_cookieName);

        if (Utils.isNotEmpty(sid)) {
            cookieSet(SessionProps.session_cookieName, sid);
        }

        return sid;
    }
}
