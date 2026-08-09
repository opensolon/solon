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

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class Cookie {
    private static final String TSPECIALS = "/()<>@,;:\\\"[]?={} \t";
    private static final String DOMAIN = "Domain"; // ;Domain=VALUE ... domain that sees cookie
    private static final String MAX_AGE = "Max-Age"; // ;Max-Age=VALUE ... cookies auto-expire
    private static final String PATH = "Path"; // ;Path=VALUE ... URLs that see the cookie
    private static final String SECURE = "Secure"; // ;Secure ... e.g. use SSL
    private static final String HTTP_ONLY = "HttpOnly";
    private static final String EMPTY_STRING = "";

    private final String name;
    private String value;
    private Map<String, String> attributes = null;


    public Cookie(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public Cookie(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Cookie setValue(final String value) {
        this.value = value;
        return this;
    }

    public String getPath() {
        return getAttribute(PATH);
    }

    public void setPath(final String uri) {
        putAttribute(PATH, uri);
    }

    public String getDomain() {
        return getAttribute(DOMAIN);
    }

    public String getAttribute(String name) {
        return attributes == null ? null : attributes.get(name);
    }

    public void setDomain(final String domain) {
        putAttribute(DOMAIN, domain != null ? domain.toLowerCase(Locale.ENGLISH) : null); // IE allegedly needs this
    }

    public void setAttribute(String name, String value) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is blank");
        }

        if (hasReservedCharacters(name)) {
            throw new IllegalArgumentException("name is invalid");
        }

        if (MAX_AGE.equalsIgnoreCase(name) && value != null) {
            setMaxAge(Integer.parseInt(value));
        } else {
            putAttribute(name, value);
        }
    }

    private static boolean hasReservedCharacters(String value) {
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if (c < 0x20 || c >= 0x7f || TSPECIALS.indexOf(c) != -1) {
                return true;
            }
        }

        return false;
    }

    private void putAttribute(String name, String value) {
        if (attributes == null) {
            attributes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        }

        if (value == null) {
            attributes.remove(name);
        } else {
            attributes.put(name, value);
        }
    }

    public int getMaxAge() {
        String maxAge = getAttribute(MAX_AGE);
        return maxAge == null ? -1 : Integer.parseInt(maxAge);
    }

    public void setMaxAge(final int expiry) {
        putAttribute(MAX_AGE, expiry < 0 ? null : String.valueOf(expiry));
    }

    public boolean isSecure() {
        return EMPTY_STRING.equals(getAttribute(SECURE));
    }

    public void setSecure(final boolean flag) {
        if (flag) {
            putAttribute(SECURE, EMPTY_STRING);
        } else {
            putAttribute(SECURE, null);
        }
    }

    public boolean isHttpOnly() {
        return EMPTY_STRING.equals(getAttribute(HTTP_ONLY));
    }

    public void setHttpOnly(final boolean httpOnly) {
        if (httpOnly) {
            putAttribute(HTTP_ONLY, EMPTY_STRING);
        } else {
            putAttribute(HTTP_ONLY, null);
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append('=').append(getValue());
        if (attributes != null) {
            sb.append(";");
            attributes.forEach((key, val) -> {
                if (MAX_AGE.equals(key)) {
                    int maxAge = getMaxAge();
                    if (maxAge > 0) {
                        sb.append("Max-Age=").append(maxAge).append(";");
                        Date expires = new Date();
                        expires.setTime(expires.getTime() + maxAge * 1000L);
                        sb.append("Expires=").append(FeatUtils.formatCookieExpire(expires)).append(";");
                    } else if (maxAge == 0) {
                        Date expires = new Date();
                        expires.setTime(0);
                        sb.append("Expires=").append(FeatUtils.formatCookieExpire(expires)).append(";");
                    }
                    return;
                }
                sb.append(key);
                if (!EMPTY_STRING.equals(val)) {
                    sb.append("=").append(val);
                }
                sb.append(";");
            });
        }
        return sb.toString();
    }
}
