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
package org.noear.solon.boot.vertx;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.noear.solon.Solon;

/**
 * @author noear
 * @since 2.7
 */
public class VxHttpHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        HttpServerResponse response = request.response();
        VxHttpContext context = new VxHttpContext(request, response);

        Solon.app().tryHandle(context);
    }
}
