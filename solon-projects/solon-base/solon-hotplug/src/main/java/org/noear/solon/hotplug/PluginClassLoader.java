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
package org.noear.solon.hotplug;

import org.noear.solon.core.AppClassLoader;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * 插件类加载器
 *
 * @author noear
 * @since 2.2
 */
public class PluginClassLoader extends AppClassLoader {
    public PluginClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        //只获取本级资源
        return findResources(name);
    }

    @Override
    public URL getResource(String name) {
        //只获取本级资源
        return findResource(name);
    }
}
