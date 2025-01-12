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

import java.io.File;

/**
 * 外接小程序信息
 *
 * @author noear
 * @since 1.8
 */
public class PluginInfo {
    /**
     * 插件名
     */
    private final String name;
    /**
     * 对应文件
     */
    private final File file;
    /**
     * 插件包
     */
    private PluginPackage addinPackage;

    public PluginInfo(String name, File file) {
        this.name = name;
        this.file = file;
    }

    /**
     * 获取名字
     */
    public String getName() {
        return name;
    }

    /**
     * 获取 jar 文件
     */
    public File getFile() {
        return file;
    }

    /**
     * 是否已开始
     */
    public boolean getStarted() {
        if (addinPackage == null) {
            return false;
        } else {
            return addinPackage.getStarted();
        }
    }


    /**
     * 获取插件包
     */
    public PluginPackage getAddinPackage() {
        return addinPackage;
    }

    ///////

    protected void setAddinPackage(PluginPackage addinPackage) {
        this.addinPackage = addinPackage;
    }
}