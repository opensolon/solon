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
package org.noear.solon.server.nettyhttp.uploadfile;

import java.io.IOException;
import java.io.InputStream;
import org.noear.solon.Solon;
import org.noear.solon.server.ServerProps;

public class HttpMultipart {
    public String name;
    public String filename;
    public HttpHeaderCollection headers;
    public InputStream body;

    public String getName() { return name; }

    public String getFilename() { return filename; }

    public HttpHeaderCollection getHeaders() { return headers; }

    public InputStream getBody() { return body; }

    public String getString() throws IOException {
        String charset = headers.getParams("Content-Type").get("charset");
        if (charset == null) {
            charset = Solon.encoding();
        }

        if (ServerProps.request_maxBodySize > Integer.MAX_VALUE) {
            return Utils.readToken(body, -1, charset, Integer.MAX_VALUE);
        } else {
            return Utils.readToken(body, -1, charset, (int) ServerProps.request_maxBodySize);
        }
    }
}
