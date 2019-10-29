/*
 * Copyright (c) 2018, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: HttpHeader.java
 * Date: 2018-02-08
 * Author: sandao
 */

package org.smartboot.http.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 三刀
 * @version V1.0 , 2018/2/8
 */
public class HttpHeaderConstant {
    public static final Map<String, HeaderNameEnum> HEADER_NAME_ENUM_MAP = new HashMap<>();
    public static final Map<String, byte[]> HEADER_NAME_EXT_MAP = new ConcurrentHashMap<>();

    static {
        for (HeaderNameEnum headerNameEnum : HeaderNameEnum.values()) {
            HEADER_NAME_ENUM_MAP.put(headerNameEnum.getName(), headerNameEnum);
        }
    }

    public interface Names {
        String ACCEPT = "Accept";
        String ACCEPT_CHARSET = "Accept-Charset";
        String ACCEPT_ENCODING = "Accept-Encoding";
        String ACCEPT_LANGUAGE = "Accept-Language";
        String ACCEPT_RANGE = "Accept-Range";
        String AGE = "Age";
        String ALLOW = "Allow";
        String AUTHORIZATION = "Authorization";

        String CACHE_CONTROL = "Cache-Control";
        String CONNECTION = "Connection";
        String CONTENT_ENCODING = "Content-Encoding";
        String CONTENT_LANGUAGE = "Content-Language";
        String CONTENT_LENGTH = "Content-Length";
        String CONTENT_LOCATION = "Content-Location";
        String CONTENT_MD5 = "Content-MD5";
        String CONTENT_RANGE = "Content-Range";
        String CONTENT_TYPE = "Content-Type";

        String DATE = "Date";

        String ETAG = "ETag";
        String EXPECT = "Expect";
        String EXPIRES = "Expires";

        String FROM = "From";

        String HOST = "Host";

        String IF_MATCH = "If-Match";
        String IF_MODIFIED_SINCE = "If-Modified-Since";
        String IF_NONE_MATCH = "If-None-Match";
        String IF_RANGE = "If-Range";
        String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

        String LAST_MODIFIED = "Last-Modified";
        String LOCATION = "Location";

        String MAX_FORWARDS = "Max-Forwards";

        String PRAGMA = "Pragma";
        String PROXY_AUTHENTICATE = "Proxy-Authenticate";
        String PROXY_AUTHORIZATION = "Proxy-Authorization";

        String RANGE = "Range";
        String REFERER = "Referer";
        String RETRY_AFTER = "Retry-After";

        String SERVER = "Server";

        String TE = "TE";
        String TRAILER = "Trailer";
        String TRANSFER_ENCODING = "Transfer-Encoding";

        String UPGRADE = "Upgrade";
        String USER_AGENT = "User-Agent";

        String VARY = "Vary";
        String VIA = "Via";

        String WARNING = "Warning";
        String WWW_AUTHENTICATE = "WWW-Authenticate";
    }

    public interface Values {
        String CHUNKED = "chunked";

        String MULTIPART_FORM_DATA = "multipart/form-data";

        String UPGRADE = "Upgrade";

        String WEBSOCKET = "websocket";

        String KEEPALIVE = "Keep-Alive";
    }


}
