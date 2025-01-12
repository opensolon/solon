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

import org.noear.solon.Solon;

class SessionProp {
    /**
     * 变量名
     * */
    public static String session_jwt_name;
    /**
     * 密钥
     * */
    public static String session_jwt_secret;
    /**
     * 前缀
     * */
    public static String session_jwt_prefix;
    /**
     * 充许超时
     * */
    public static boolean session_jwt_allowExpire;
    /**
     * 充许自动发布（即输出到header或cookie）
     * */
    public static boolean session_jwt_allowAutoIssue;
    /**
     * 充许使用header承载
     * */
    public static boolean session_jwt_allowUseHeader;


    public static void init() {
        session_jwt_name = Solon.cfg().get("server.session.state.jwt.name", "TOKEN");
        session_jwt_secret = Solon.cfg().get("server.session.state.jwt.secret");
        session_jwt_prefix = Solon.cfg().get("server.session.state.jwt.prefix", "").trim();

        session_jwt_allowExpire = Solon.cfg().getBool("server.session.state.jwt.allowExpire", true);
        session_jwt_allowUseHeader = Solon.cfg().getBool("server.session.state.jwt.allowUseHeader", false);

        //兼容旧版本（以后会被弃用）
        boolean issueOld = Solon.cfg().getBool("server.session.state.jwt.allowIssue", true);

        boolean issueNew = Solon.cfg().getBool("server.session.state.jwt.allowAutoIssue", true);
        session_jwt_allowAutoIssue = (issueOld && issueNew);

        //兼容旧版本（以后会被弃用）
        boolean requestUseHeader = Solon.cfg().getBool("server.session.state.jwt.requestUseHeader", false);
        //兼容旧版本（以后会被弃用）
        boolean responseUseHeader = Solon.cfg().getBool("server.session.state.jwt.responseUseHeader", false);

        if (requestUseHeader || responseUseHeader) {
            session_jwt_allowUseHeader = true;
        }
    }
}
