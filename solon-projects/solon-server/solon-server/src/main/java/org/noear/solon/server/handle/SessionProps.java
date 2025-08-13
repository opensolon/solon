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

import org.noear.solon.Solon;
import org.noear.solon.Utils;

/**
 * Web 会话状态基本属性
 *
 * @author noear
 * @since 1.10
 * @since 3.5
 */
public class SessionProps {
    public final static int session_timeout;

    public final static String session_cookieName;
    public final static String session_cookieDomain;

    public final static boolean session_cookieDomainAuto;


    static {
        session_timeout = Solon.cfg().getInt("server.session.timeout", 60 * 60 * 2);

        session_cookieName = Solon.cfg().get("server.session.cookieName", "SOLONID");

        //
        // cookieDomain
        //
        String tmp = Solon.cfg().get("server.session.cookieDomain");
        if (Utils.isEmpty(tmp)) {
            //@Deprecated //但不删
            tmp = Solon.cfg().get("server.session.state.domain");
        }

        session_cookieDomain = tmp;


        //
        // cookieDomainAuto
        //
        tmp = Solon.cfg().get("server.session.cookieDomainAuto");
        if (Utils.isEmpty(tmp)) {
            //@Deprecated //但不删
            tmp = Solon.cfg().get("server.session.state.domain.auto");
        }

        if (Utils.isEmpty(tmp)) {
            session_cookieDomainAuto = true;
        } else {
            session_cookieDomainAuto = Boolean.parseBoolean(tmp);
        }
    }
}
