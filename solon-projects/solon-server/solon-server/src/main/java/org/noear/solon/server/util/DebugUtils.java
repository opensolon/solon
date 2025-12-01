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
package org.noear.solon.server.util;

import org.noear.solon.Solon;
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
 * @since 3.5
 */
public class DebugUtils {
    /**
     * 是否为测试地址
     *
     * @since 3.7
     */
    public static boolean isTestLocation(URL url) {
        if (url.getPath().contains("/target/test-classes/") ||
                url.getPath().contains("/build/classes/test/")) {
            return true;
        } else {
            return false;
        }
    }

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
        URL sourceLocation = null;

        if (Solon.app() == null) {
            sourceLocation = ResourceUtil.getResource(classLoader, "/");
        } else {
            sourceLocation = Solon.app().sourceLocation();
        }

        if (sourceLocation == null) {
            return null;
        }

        String userdir = System.getProperty("user.dir");
        File dir = null;

        if (pathPrefix.charAt(0) != '/') {
            pathPrefix = "/" + pathPrefix;
        }

        if (isTestLocation(sourceLocation)) {
            String dir_str = userdir + "/src/test/resources" + pathPrefix;
            dir = new File(dir_str);
            if (dir.exists() == false) {
                dir_str = userdir + "/src/test/webapp" + pathPrefix;
                dir = new File(dir_str);
            }

            if (dir.exists() == false) {
                return null;
            }
        } else {
            String dir_str = userdir + "/src/main/resources" + pathPrefix;
            dir = new File(dir_str);
            if (dir.exists() == false) {
                dir_str = userdir + "/src/main/webapp" + pathPrefix;
                dir = new File(dir_str);
            }

            if (dir.exists() == false) {
                return null;
            }
        }

        return dir;
    }
}