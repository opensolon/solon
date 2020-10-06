/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: CookieImpl.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server;

import org.smartboot.http.Cookie;

import java.util.Date;

/**
 * @author 三刀
 * @version V1.0 , 2018/8/31
 */
 class CookieImpl implements Cookie {

    private final String name;
    private String value;
    private String path;
    private String domain;
    private Integer maxAge;
    private Date expires;
    private boolean discard;
    private boolean secure;
    private boolean httpOnly;
    private int version = 0;
    private String comment;


    public CookieImpl(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public CookieImpl(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public CookieImpl setValue(final String value) {
        this.value = value;
        return this;
    }

    public String getPath() {
        return path;
    }

    public CookieImpl setPath(final String path) {
        this.path = path;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public CookieImpl setDomain(final String domain) {
        this.domain = domain;
        return this;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public CookieImpl setMaxAge(final Integer maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public boolean isDiscard() {
        return discard;
    }

    public CookieImpl setDiscard(final boolean discard) {
        this.discard = discard;
        return this;
    }

    public boolean isSecure() {
        return secure;
    }

    public CookieImpl setSecure(final boolean secure) {
        this.secure = secure;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public CookieImpl setVersion(final int version) {
        this.version = version;
        return this;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public CookieImpl setHttpOnly(final boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    public Date getExpires() {
        return expires;
    }

    public CookieImpl setExpires(final Date expires) {
        this.expires = expires;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public Cookie setComment(final String comment) {
        this.comment = comment;
        return this;
    }
}
