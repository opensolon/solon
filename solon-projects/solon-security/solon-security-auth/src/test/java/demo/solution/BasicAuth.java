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
package demo.solution;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;

import java.util.Base64;

/**
 * Authorization: Basic MTIzOjEyMw==
 *
 * @author noear
 * @since 1.4
 */
public class BasicAuth {
    private static final String TYPE = "Basic ";

    public void auth(Context ctx) {
        String auth0 = ctx.header("Authorization");

        if (Utils.isEmpty(auth0) || auth0.startsWith(TYPE) == false) {
            ctx.status(401);
            ctx.headerSet("WWW-Authenticate", "Basic realm...");
            return;
        }

        String auth1 = auth0.substring(TYPE.length()).trim();
        String auth2 = new String(Base64.getDecoder().decode(auth1));

        String username = auth2.split(":")[0];
        String paasword = auth2.split(":")[0];

    }
}
