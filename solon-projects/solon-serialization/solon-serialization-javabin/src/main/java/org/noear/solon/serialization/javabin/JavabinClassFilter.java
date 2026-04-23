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
package org.noear.solon.serialization.javabin;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Javabin 反序列化类过滤器。
 */
public class JavabinClassFilter {

    private final Set<String> allowPatterns = new LinkedHashSet<>();
    private final Set<String> denyPatterns = new LinkedHashSet<>();
    private boolean allowAll;

    public static JavabinClassFilter defaults() {
        JavabinClassFilter f = new JavabinClassFilter();
        f.allow("java.lang.");
        f.allow("java.util.");
        f.allow("java.time.");
        f.allow("java.math.");
        f.allow("java.net.URI");
        f.allow("java.net.URL");
        f.allow("java.net.InetAddress");
        f.allow("java.net.Inet4Address");
        f.allow("java.net.Inet6Address");
        f.allow("java.sql.");

        f.deny("java.lang.Runtime");
        f.deny("java.lang.Process");
        f.deny("java.lang.ProcessBuilder");
        f.deny("java.lang.ProcessImpl");
        f.deny("java.lang.UNIXProcess");
        f.deny("javax.management.");
        f.deny("javax.naming.");
        f.deny("javax.script.");
        f.deny("com.sun.rowset.");
        f.deny("sun.reflect.annotation.");
        f.deny("sun.rmi.");
        f.deny("java.rmi.");
        return f;
    }

    public static JavabinClassFilter unrestricted() {
        JavabinClassFilter f = new JavabinClassFilter();
        f.allowAll = true;
        return f;
    }

    public JavabinClassFilter allow(String pattern) {
        if (pattern != null && pattern.length() > 0) {
            allowPatterns.add(pattern);
        }
        return this;
    }

    public JavabinClassFilter deny(String pattern) {
        if (pattern != null && pattern.length() > 0) {
            denyPatterns.add(pattern);
        }
        return this;
    }

    public JavabinClassFilter allowAll(boolean allowAll) {
        this.allowAll = allowAll;
        return this;
    }

    public boolean isAllowAll() {
        return allowAll;
    }

    public boolean isAllowed(String className) {
        if (className == null || className.length() == 0) {
            return false;
        }

        String t = className;
        while (t.length() > 0 && t.charAt(0) == '[') {
            t = t.substring(1);
        }
        if (t.length() == 0) {
            return false;
        }
        if (t.length() == 1) {
            return true;
        }
        if (t.charAt(0) == 'L' && t.charAt(t.length() - 1) == ';') {
            t = t.substring(1, t.length() - 1);
        }

        for (String deny : denyPatterns) {
            if (matches(t, deny)) {
                return false;
            }
        }
        if (allowAll) {
            return true;
        }
        for (String allow : allowPatterns) {
            if (matches(t, allow)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matches(String name, String pattern) {
        if (pattern.endsWith(".")) {
            return name.startsWith(pattern);
        }
        if (name.equals(pattern)) {
            return true;
        }
        return name.length() > pattern.length()
                && name.startsWith(pattern)
                && name.charAt(pattern.length()) == '$';
    }
}
