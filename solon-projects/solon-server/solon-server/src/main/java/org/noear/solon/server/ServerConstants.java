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
package org.noear.solon.server;

/**
 * @author noear
 * @since 1.6
 * @since 3.5
 */
public interface ServerConstants {
    String SESSION_LAST_ACCESS_TIME = "SESSION_LAST_ACCESS_TIME";
    String SESSION_CREATION_TIME = "SESSION_CREATION_TIME";


    String SERVER_SSL_KEY_STORE = "server.ssl.keyStore";
    String SERVER_SSL_KEY_TYPE = "server.ssl.keyType";
    String SERVER_SSL_KEY_PASSWORD = "server.ssl.keyPassword";

    String SERVER_HTTP_GZIP_ENABLE = "server.http.gzip.enable";
    String SERVER_HTTP_GZIP_MINSIZE = "server.http.gzip.minSize";
    String SERVER_HTTP_GZIP_MIMETYPES = "server.http.gzip.mimeTypes";

    String SERVER_REQUEST_MAXHEADERSIZE = "server.request.maxHeaderSize";
    String SERVER_REQUEST_MAXBODYSIZE = "server.request.maxBodySize";
    String SERVER_REQUEST_MAXFILESIZE = "server.request.maxFileSize";
    String SERVER_REQUEST_USETEMPFILE = "server.request.useTempfile";
    String SERVER_REQUEST_USERAWPATH = "server.request.useRawpath";
    String SERVER_REQUEST_ENCODING = "server.request.encoding";

    String SIGNAL_HTTP = "http";
    String SIGNAL_SOCKET = "socket";
    String SIGNAL_WEBSOCKET = "websocket";

    int SIGNAL_LIFECYCLE_INDEX = 99;
}