/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.common;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class HeaderName {
    public static final HeaderName ACCEPT = new HeaderName("Accept");
    public static final HeaderName ACCEPT_CHARSET = new HeaderName("Accept-Charset");
    public static final HeaderName ACCEPT_ENCODING = new HeaderName("Accept-Encoding");
    public static final HeaderName ACCEPT_LANGUAGE = new HeaderName("Accept-Language");
    public static final HeaderName ACCEPT_RANGES = new HeaderName("Accept-Ranges");
    public static final HeaderName AGE = new HeaderName("Age");
    public static final HeaderName ALLOW = new HeaderName("Allow");
    public static final HeaderName AUTHORIZATION = new HeaderName("Authorization");
    public static final HeaderName CACHE_CONTROL = new HeaderName("Cache-Control");
    public static final HeaderName CONNECTION = new HeaderName("Connection");
    public static final HeaderName CONTENT_ENCODING = new HeaderName("Content-Encoding");
    public static final HeaderName CONTENT_LANGUAGE = new HeaderName("Content-Language");
    public static final HeaderName CONTENT_LENGTH = new HeaderName("Content-Length");
    public static final HeaderName CONTENT_LOCATION = new HeaderName("Content-Location");
    public static final HeaderName CONTENT_MD5 = new HeaderName("Content-MD5");
    public static final HeaderName CONTENT_RANGE = new HeaderName("Content-Range");
    public static final HeaderName CONTENT_TYPE = new HeaderName("Content-Type");
    public static final HeaderName DATE = new HeaderName("Date");
    public static final HeaderName ETAG = new HeaderName("ETag");
    public static final HeaderName EXPECT = new HeaderName("Expect");
    public static final HeaderName EXPIRES = new HeaderName("Expires");
    public static final HeaderName FROM = new HeaderName("From");
    public static final HeaderName HOST = new HeaderName("Host");
    public static final HeaderName IF_MATCH = new HeaderName("If-Match");
    public static final HeaderName IF_MODIFIED_SINCE = new HeaderName("If-Modified-Since");
    public static final HeaderName IF_NONE_MATCH = new HeaderName("If-None-Match");
    public static final HeaderName IF_RANGE = new HeaderName("If-Range");
    public static final HeaderName IF_UNMODIFIED_SINCE = new HeaderName("If-Unmodified-Since");
    public static final HeaderName LAST_MODIFIED = new HeaderName("Last-Modified");
    public static final HeaderName LOCATION = new HeaderName("Location");
    public static final HeaderName MAX_FORWARDS = new HeaderName("Max-Forwards");
    public static final HeaderName PRAGMA = new HeaderName("Pragma");
    public static final HeaderName PROXY_AUTHENTICATE = new HeaderName("Proxy-Authenticate");
    public static final HeaderName PROXY_AUTHORIZATION = new HeaderName("Proxy-Authorization");
    public static final HeaderName RANGE = new HeaderName("Range");
    public static final HeaderName REFERER = new HeaderName("Referer");
    public static final HeaderName RETRY_AFTER = new HeaderName("Retry-After");
    public static final HeaderName SERVER = new HeaderName("Server");
    public static final HeaderName TE = new HeaderName("TE");
    public static final HeaderName TRAILER = new HeaderName("Trailer");
    public static final HeaderName TRANSFER_ENCODING = new HeaderName("Transfer-Encoding");
    public static final HeaderName UPGRADE = new HeaderName("Upgrade");
    public static final HeaderName USER_AGENT = new HeaderName("User-Agent");
    public static final HeaderName VARY = new HeaderName("Vary");
    public static final HeaderName VIA = new HeaderName("Via");
    public static final HeaderName WARNING = new HeaderName("Warning");
    public static final HeaderName WWW_AUTHENTICATE = new HeaderName("WWW-Authenticate");
    public static final HeaderName Sec_WebSocket_Accept = new HeaderName("Sec-WebSocket-Accept");
    public static final HeaderName COOKIE = new HeaderName("Cookie");
    public static final HeaderName SET_COOKIE = new HeaderName("Set-Cookie");
    public static final HeaderName Sec_WebSocket_Key = new HeaderName("Sec-WebSocket-Key");
    public static final HeaderName Sec_WebSocket_Protocol = new HeaderName("Sec-WebSocket-Protocol");
    public static final HeaderName Sec_WebSocket_Version = new HeaderName("Sec-WebSocket-Version");
    public static final HeaderName HTTP2_SETTINGS = new HeaderName("HTTP2-Settings");
    public static final HeaderName CONTENT_DISPOSITION = new HeaderName("Content-Disposition");

    private final String name;

    private final String lowCaseName;

    HeaderName(String name) {
        this.name = name;
        this.lowCaseName = name.toLowerCase();
    }


    public String getName() {
        return name;
    }

    public String getLowCaseName() {
        return lowCaseName;
    }
}
