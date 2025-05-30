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
package org.noear.solon.core.exception;

import org.noear.solon.exception.SolonException;

/**
 * 状态异常（用传递处理状态，如 4xx）
 *
 * @author noear
 * @since 2.4
 */
public class StatusException extends SolonException {
    public static final int CODE_CONTINUE = 100;
    public static final int CODE_SWITCHING_PROTOCOLS = 101;
    public static final int CODE_OK = 200;
    public static final int CODE_CREATED = 201;
    public static final int CODE_ACCEPTED = 202;
    public static final int CODE_NON_AUTHORITATIVE_INFORMATION = 203;
    public static final int CODE_NO_CONTENT = 204;
    public static final int CODE_RESET_CONTENT = 205;
    public static final int CODE_PARTIAL_CONTENT = 206;
    public static final int CODE_MULTIPLE_CHOICES = 300;
    public static final int CODE_MOVED_PERMANENTLY = 301;
    public static final int CODE_MOVED_TEMPORARILY = 302;
    public static final int CODE_FOUND = 302;
    public static final int CODE_SEE_OTHER = 303;
    public static final int CODE_NOT_MODIFIED = 304;
    public static final int CODE_USE_PROXY = 305;
    public static final int CODE_TEMPORARY_REDIRECT = 307;
    public static final int CODE_BAD_REQUEST = 400;
    public static final int CODE_UNAUTHORIZED = 401;
    public static final int CODE_PAYMENT_REQUIRED = 402;
    public static final int CODE_FORBIDDEN = 403;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_METHOD_NOT_ALLOWED = 405;
    public static final int CODE_NOT_ACCEPTABLE = 406;
    public static final int CODE_PROXY_AUTHENTICATION_REQUIRED = 407;
    public static final int CODE_REQUEST_TIMEOUT = 408;
    public static final int CODE_CONFLICT = 409;
    public static final int CODE_GONE = 410;
    public static final int CODE_LENGTH_REQUIRED = 411;
    public static final int CODE_PRECONDITION_FAILED = 412;
    public static final int CODE_REQUEST_ENTITY_TOO_LARGE = 413;
    public static final int CODE_REQUEST_URI_TOO_LONG = 414;
    public static final int CODE_UNSUPPORTED_MEDIA_TYPE = 415;
    public static final int CODE_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    public static final int CODE_EXPECTATION_FAILED = 417;
    public static final int CODE_INTERNAL_SERVER_ERROR = 500;
    public static final int CODE_NOT_IMPLEMENTED = 501;
    public static final int CODE_BAD_GATEWAY = 502;
    public static final int CODE_SERVICE_UNAVAILABLE = 503;
    public static final int CODE_GATEWAY_TIMEOUT = 504;
    public static final int CODE_HTTP_VERSION_NOT_SUPPORTED = 505;


    private int code;

    /**
     * 获取状态码
     */
    public int getCode() {
        return code;
    }

    public StatusException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public StatusException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public StatusException(String message, int code) {
        super(message);
        this.code = code;
    }
}