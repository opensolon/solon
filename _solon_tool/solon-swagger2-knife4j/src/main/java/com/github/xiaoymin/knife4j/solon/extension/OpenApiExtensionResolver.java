package com.github.xiaoymin.knife4j.solon.extension;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.bean.InitializingBean;

/**
 * @author noear
 * @since 2.2
 */
@Component
public class OpenApiExtensionResolver implements InitializingBean {

    @Inject(value = "${knife4j.setting}",required = false)
    OpenApiExtendSetting setting = new OpenApiExtendSetting();

    OpenApiExtension extension = new OpenApiExtension();

    public OpenApiExtendSetting getSetting() {
        return setting;
    }

    public OpenApiExtension getExtension() {
        return extension;
    }

    @Override
    public void afterInjection() throws Throwable {
        extension.addProperty(new OpenApiSettingExtension(setting));
    }
}
