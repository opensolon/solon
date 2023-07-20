package com.github.xiaoymin.knife4j.solon.extension;

import com.github.xiaoymin.knife4j.solon.settings.OpenApiExtendSetting;
import org.noear.solon.docs.models.ApiVendorExtension;

/**
 * @author noear
 * @since 2.3
 */
public class OpenApiSettingExtension implements ApiVendorExtension<OpenApiExtendSetting> {
    OpenApiExtendSetting value;
    public OpenApiSettingExtension(OpenApiExtendSetting value){
        this.value = value;
    }
    @Override
    public String getName() {
        return "x-setting";
    }

    @Override
    public OpenApiExtendSetting getValue() {
        return value;
    }
}
