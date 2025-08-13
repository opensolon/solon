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
package org.noear.solon.boot.web;

import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * Web 调试模式工具
 *
 * @author noear
 * @since 2.9
 * @deprecated 3.5
 * */
@Deprecated
public class DebugUtils {
    /**
     * 获取视图调试位置
     *
     * @param classLoader 类加载器
     * @param pathPrefix  路径前缀
     */
    public static File getDebugLocation(ClassLoader classLoader, String pathPrefix) {
        if (Utils.isEmpty(pathPrefix)) {
            return null;
        }

        if (ResourceUtil.hasFile(pathPrefix)) {
            return null;
        }

        //添加调试模式
        URL rooturi = ResourceUtil.getResource(classLoader, "/");
        if (rooturi == null) {
            return null;
        }

        String rootdir = rooturi.toString();

        if (rootdir.contains("target/classes/")) {
            //兼容 maven
            rootdir = rootdir.replace("target/classes/", "");
        } else if (rootdir.contains("build/classes/java/main/")) {
            //兼容 gradle
            rootdir = rootdir.replace("build/classes/java/main/", "");
        }

        File dir = null;

        if (rootdir.startsWith("file:")) {
            if (pathPrefix.charAt(0) != '/') {
                pathPrefix = "/" + pathPrefix;
            }

            String dir_str = rootdir + "src/main/resources" + pathPrefix;
            dir = new File(URI.create(dir_str));
            if (dir.exists() == false) {
                dir_str = rootdir + "src/main/webapp" + pathPrefix;
                dir = new File(URI.create(dir_str));
            }

            if (dir.exists() == false) {
                return null;
            }
        }

        return dir;
    }
}