/*
 * Copyright 2017-2024 noear.org and authors
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
package com.github.xiaoymin.knife4j.solon.extension;

import com.github.xiaoymin.knife4j.solon.settings.OpenApiExtendSetting;
import com.github.xiaoymin.knife4j.solon.settings.OpenApiSetting;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.bean.InitializingBean;
import org.noear.solon.docs.models.ApiVendorExtension;
import org.noear.solon.docs.DocDocket;

import java.util.Arrays;
import java.util.List;

/**
 * @author noear
 * @since 2.3
 */
@Component
public class OpenApiExtensionResolver implements InitializingBean {

    @Inject(value = "${knife4j}", required = false)
    OpenApiSetting setting = new OpenApiSetting();

    @Inject(value = "${knife4j.setting}", required = false)
    OpenApiExtendSetting extendSetting = new OpenApiExtendSetting();

    OpenApiExtension extension = new OpenApiExtension();


    @Override
    public void afterInjection() throws Throwable {
        extension.addProperty(new OpenApiSettingExtension(extendSetting));
    }


    public OpenApiSetting getSetting() {
        return setting;
    }

    public OpenApiExtendSetting getExtendSetting() {
        return extendSetting;
    }

    public List<ApiVendorExtension> buildExtensions() {
        return Arrays.asList(extension);
    }

    public void buildExtensions(DocDocket docket) {
        if (setting.getBasic().isEnable()) {
            docket.basicAuth(setting.getBasic().getUsername(),
                    setting.getBasic().getPassword());
        }


        docket.vendorExtensions(extension.getName(), extension.getValue());
    }
}
