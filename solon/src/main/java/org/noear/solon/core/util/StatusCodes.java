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
package org.noear.solon.core.util;

/**
 * 状态码
 *
 * @author noear
 * @since 3.3
 */
public interface StatusCodes {
    int CODE_CONTINUE = 100;
    int CODE_SWITCHING_PROTOCOLS = 101;
    int CODE_OK = 200;
    int CODE_CREATED = 201;
    int CODE_ACCEPTED = 202;
    int CODE_NON_AUTHORITATIVE_INFORMATION = 203;
    int CODE_NO_CONTENT = 204;
    int CODE_RESET_CONTENT = 205;
    int CODE_PARTIAL_CONTENT = 206;
    int CODE_MULTIPLE_CHOICES = 300;
    int CODE_MOVED_PERMANENTLY = 301;
    int CODE_MOVED_TEMPORARILY = 302;
    int CODE_FOUND = 302;
    int CODE_SEE_OTHER = 303;
    int CODE_NOT_MODIFIED = 304;
    int CODE_USE_PROXY = 305;
    int CODE_TEMPORARY_REDIRECT = 307;
    int CODE_BAD_REQUEST = 400;
    int CODE_UNAUTHORIZED = 401;
    int CODE_PAYMENT_REQUIRED = 402;
    int CODE_FORBIDDEN = 403;
    int CODE_NOT_FOUND = 404;
    int CODE_METHOD_NOT_ALLOWED = 405;
    int CODE_NOT_ACCEPTABLE = 406;
    int CODE_PROXY_AUTHENTICATION_REQUIRED = 407;
    int CODE_REQUEST_TIMEOUT = 408;
    int CODE_CONFLICT = 409;
    int CODE_GONE = 410;
    int CODE_LENGTH_REQUIRED = 411;
    int CODE_PRECONDITION_FAILED = 412;
    int CODE_REQUEST_ENTITY_TOO_LARGE = 413;
    int CODE_REQUEST_URI_TOO_LONG = 414;
    int CODE_UNSUPPORTED_MEDIA_TYPE = 415;
    int CODE_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    int CODE_EXPECTATION_FAILED = 417;
    int CODE_INTERNAL_SERVER_ERROR = 500;
    int CODE_NOT_IMPLEMENTED = 501;
    int CODE_BAD_GATEWAY = 502;
    int CODE_SERVICE_UNAVAILABLE = 503;
    int CODE_GATEWAY_TIMEOUT = 504;
    int CODE_HTTP_VERSION_NOT_SUPPORTED = 505;
}
