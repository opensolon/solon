/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: Cookies.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server;

import org.smartboot.http.Cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


class Cookies {

    public static final String DOMAIN = "$Domain";
    public static final String VERSION = "$Version";
    public static final String PATH = "$Path";


    private Cookies() {

    }

    public static Map<String, Cookie> parseRequestCookies(boolean allowEqualInValue, String cookies) {
        if (cookies == null) {
            return new TreeMap<>();
        }
        final Map<String, Cookie> parsedCookies = new TreeMap<>();
        parseCookie(cookies, parsedCookies, allowEqualInValue, true);
        return parsedCookies;
    }


    private static void parseCookie(final String cookie, final Map<String, Cookie> parsedCookies, boolean allowEqualInValue, boolean commaIsSeperator) {
        int state = 0;
        String name = null;
        int start = 0;
        boolean containsEscapedQuotes = false;
        int cookieCount = parsedCookies.size();
        final Map<String, String> cookies = new HashMap<>();
        final Map<String, String> additional = new HashMap<>();
        for (int i = 0; i < cookie.length(); ++i) {
            char c = cookie.charAt(i);
            switch (state) {
                case 0: {
                    //eat leading whitespace
                    if (c == ' ' || c == '\t' || c == ';') {
                        start = i + 1;
                        break;
                    }
                    state = 1;
                    //fall through
                }
                case 1: {
                    //extract key
                    if (c == '=') {
                        name = cookie.substring(start, i);
                        start = i + 1;
                        state = 2;
                    } else if (c == ';' || (commaIsSeperator && c == ',')) {
                        if (name != null) {
                            cookieCount = createCookie(name, cookie.substring(start, i), cookieCount, cookies, additional);
                        }
                        state = 0;
                        start = i + 1;
                    }
                    break;
                }
                case 2: {
                    //extract value
                    if (c == ';' || (commaIsSeperator && c == ',')) {
                        cookieCount = createCookie(name, cookie.substring(start, i), cookieCount, cookies, additional);
                        state = 0;
                        start = i + 1;
                    } else if (c == '"' && start == i) { //only process the " if it is the first character
                        containsEscapedQuotes = false;
                        state = 3;
                        start = i + 1;
                    } else if (!allowEqualInValue && c == '=') {
                        cookieCount = createCookie(name, cookie.substring(start, i), cookieCount, cookies, additional);
                        state = 4;
                        start = i + 1;
                    }
                    break;
                }
                case 3: {
                    //extract quoted value
                    if (c == '"') {
                        cookieCount = createCookie(name, containsEscapedQuotes ? unescapeDoubleQuotes(cookie.substring(start, i)) : cookie.substring(start, i), cookieCount, cookies, additional);
                        state = 0;
                        start = i + 1;
                    }
                    // Skip the next double quote char '"' when it is escaped by backslash '\' (i.e. \") inside the quoted value
                    if (c == '\\' && (i + 1 < cookie.length()) && cookie.charAt(i + 1) == '"') {
                        // But..., do not skip at the following conditions
                        if (i + 2 == cookie.length()) { // Cookie: key="\" or Cookie: key="...\"
                            break;
                        }
                        if (i + 2 < cookie.length() && (cookie.charAt(i + 2) == ';'      // Cookie: key="\"; key2=...
                                || (commaIsSeperator && cookie.charAt(i + 2) == ','))) { // Cookie: key="\", key2=...
                            break;
                        }
                        // Skip the next double quote char ('"' behind '\') in the cookie value
                        i++;
                        containsEscapedQuotes = true;
                    }
                    break;
                }
                case 4: {
                    //skip value portion behind '='
                    if (c == ';' || (commaIsSeperator && c == ',')) {
                        state = 0;
                    }
                    start = i + 1;
                    break;
                }
            }
        }
        if (state == 2) {
            createCookie(name, cookie.substring(start), cookieCount, cookies, additional);
        }

        for (final Map.Entry<String, String> entry : cookies.entrySet()) {
            Cookie c = new CookieImpl(entry.getKey(), entry.getValue());
            String domain = additional.get(DOMAIN);
            if (domain != null) {
                c.setDomain(domain);
            }
            String version = additional.get(VERSION);
            if (version != null) {
                c.setVersion(Integer.parseInt(version));
            }
            String path = additional.get(PATH);
            if (path != null) {
                c.setPath(path);
            }
            parsedCookies.put(c.getName(), c);
        }
    }

    private static int createCookie(final String name, final String value, int cookieCount,
                                    final Map<String, String> cookies, final Map<String, String> additional) {
        if (!name.isEmpty() && name.charAt(0) == '$') {
            if (additional.containsKey(name)) {
                return cookieCount;
            }
            additional.put(name, value);
            return cookieCount;
        } else {
            if (cookies.containsKey(name)) {
                return cookieCount;
            }
            cookies.put(name, value);
            return ++cookieCount;
        }
    }

    private static String unescapeDoubleQuotes(final String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // Replace all escaped double quote (\") to double quote (")
        char[] tmp = new char[value.length()];
        int dest = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '\\' && (i + 1 < value.length()) && value.charAt(i + 1) == '"') {
                i++;
            }
            tmp[dest] = value.charAt(i);
            dest++;
        }
        return new String(tmp, 0, dest);
    }
}
