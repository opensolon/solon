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
package org.noear.solon.extend.impl;

import org.noear.solon.Solon;
import org.noear.solon.core.PropsLoader;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.config.yaml.PropertiesJson;
import org.noear.solon.config.yaml.PropertiesYaml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Properties;

/**
 * 属性加载器-静态扩展实现（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
 *
 * @author noear
 * @since 1.5
 */
public class PropsLoaderExt extends PropsLoader {

    @Override
    public boolean isSupport(String suffix) {
        if (suffix == null) {
            return false;
        }

        return suffix.endsWith(".properties") || suffix.endsWith(".yml") || suffix.endsWith(".yaml");
    }

    @Override
    public Properties load(URL url) throws IOException {
        if (url == null) {
            return null;
        }

        String fileName = url.toString();

        if (fileName.endsWith(".properties")) {
            if (Solon.app() != null && Solon.cfg().isDebugMode()) {
                LogUtil.global().trace(fileName);
            }

            Properties tmp = new Properties();
            try (InputStreamReader reader = new InputStreamReader(url.openStream(), Solon.encoding())) {
                tmp.load(reader);
            }
            return tmp;
        }

        if (fileName.endsWith(".yml") || fileName.endsWith(".yaml")) {
            if (Solon.app() != null && Solon.cfg().isDebugMode()) {
                LogUtil.global().trace(fileName);
            }

            PropertiesYaml tmp = new PropertiesYaml();
            try (InputStreamReader reader = new InputStreamReader(url.openStream(), Solon.encoding())) {
                tmp.loadYml(reader);
            }
            return tmp;
        }

        throw new RuntimeException("This profile is not supported: " + fileName);
    }

    @Override
    public Properties build(String text) throws IOException {
        text = text.trim();

        int idx1 = text.indexOf("=");
        int idx2 = text.indexOf(":");

        //有{开头
        if (text.startsWith("{") && text.endsWith("}")) {
            PropertiesJson tmp = new PropertiesJson();
            tmp.loadJson(text);
            return tmp;
        }

        //有[开头
        if (text.startsWith("[") && text.endsWith("]")) {
            PropertiesJson tmp = new PropertiesJson();
            tmp.loadJson(text);
            return tmp;
        }


        //有=
        if (idx1 > 0 && (idx1 < idx2 || idx2 < 0)) {
            Properties tmp = new Properties();
            tmp.load(new StringReader(text));
            return tmp;
        }

        //有:
        if (idx2 > 0 && (idx2 < idx1 || idx1 < 0)) {
            PropertiesYaml tmp = new PropertiesYaml();
            tmp.loadYml(new StringReader(text));
            return tmp;
        }

        return new Properties();
    }
}
