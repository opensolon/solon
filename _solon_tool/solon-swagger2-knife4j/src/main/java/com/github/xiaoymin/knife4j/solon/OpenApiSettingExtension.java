package com.github.xiaoymin.knife4j.solon;

import org.noear.solon.docs.ApiVendorExtension;

/**
 * @author noear 2023/5/24 created
 */
public class OpenApiSettingExtension implements ApiVendorExtension {
    OpenApiExtendSetting value;
    public OpenApiSettingExtension(OpenApiExtendSetting value){
        this.value = value;
    }
    @Override
    public String getName() {
        return "x-setting";
    }

    @Override
    public Object getValue() {
        return value;
    }
}
