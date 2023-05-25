package com.github.xiaoymin.knife4j.solon.settings;

/**
 * @author noear
 * @since 2.3
 */
public class OpenApiSetting {
    private boolean enable = true;
    private boolean production = false;
    private OpenApiBasicAuth basic = new OpenApiBasicAuth();

    public boolean isEnable() {
        return enable;
    }

    public boolean isProduction() {
        return production;
    }

    public OpenApiBasicAuth getBasic() {
        return basic;
    }
}
