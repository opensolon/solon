package com.github.xiaoymin.knife4j.solon.settings;

import org.noear.solon.docs.BasicAuth;

/**
 * @author noear
 * @since 2.3
 */
public class OpenApiBasicAuth implements BasicAuth {
    boolean enable = true;
    String username;
    String password;

    public boolean isEnable() {
        return enable;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
