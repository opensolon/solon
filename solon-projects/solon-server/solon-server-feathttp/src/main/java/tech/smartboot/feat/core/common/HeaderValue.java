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
public class HeaderValue {
    public static final String CONTINUE = "100-continue";
    public static final String NO_CACHE = "no-cache";

    public interface TransferEncoding {
        String CHUNKED = "chunked";
    }

    public interface Upgrade {
        String WEBSOCKET = "websocket";
        String H2 = "h2";
        String H2C = "h2c";
    }

    public interface ContentEncoding {
        String GZIP = "gzip";
    }

    public interface Connection {
        String UPGRADE = "Upgrade";
        String KEEPALIVE = "Keep-Alive";
        String keepalive = "keep-alive";
        String CLOSE = "close";
    }

    public interface ContentType {
        String MULTIPART_FORM_DATA = "multipart/form-data";
        String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
        String APPLICATION_JSON = "application/json";
        String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
        String TEXT_HTML_UTF8 = "text/html; charset=utf-8";
        String TEXT_PLAIN_UTF8 = "text/plain; charset=UTF-8";
        String EVENT_STREAM = "text/event-stream";
    }

    /**
     * name
     */
    private String name;
    /**
     * Value 值
     */
    private String value;
    /**
     * 同名Value
     */
    private HeaderValue nextValue;

    public HeaderValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HeaderValue getNextValue() {
        return nextValue;
    }

    public void setNextValue(HeaderValue nextValue) {
        this.nextValue = nextValue;
    }
}
