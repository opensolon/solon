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
package org.noear.solon.web.staticfiles.repository;

import org.noear.solon.Solon;
import org.noear.solon.boot.web.DebugUtils;
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.web.staticfiles.StaticRepository;

import java.io.File;
import java.net.URL;

/**
 * 类路径型静态仓库（支持位置例：static 或 /static 或 /static/）
 *
 * @author noear
 * @since 1.5
 */
public class ClassPathStaticRepository implements StaticRepository {
    private String location;
    private File locationDebug;
    ClassLoader classLoader;


    /**
     * 构建函数
     *
     * @param location 位置
     */
    public ClassPathStaticRepository(String location) {
        this(AppClassLoader.global(), location);
    }

    public ClassPathStaticRepository(ClassLoader classLoader, String location) {
        this.classLoader = classLoader;

        setLocation(location);
    }

    /**
     * @param location 位置
     */
    protected void setLocation(String location) {
        if (location == null) {
            return;
        }

        // 去掉头尾的 "/"
        if (location.startsWith("/")) {
            location = location.substring(1);
        }

        //尾部加上 "/"（确保'/'结尾）
        if (location.endsWith("/") == false) {
            location = location + "/";
        }

        this.location = location;

        if (Solon.cfg().isDebugMode()) {
            this.locationDebug = DebugUtils.getDebugLocation(classLoader, location);
        }
    }

    /**
     * @param relativePath 例：demo/file.htm （没有'/'开头）
     */
    @Override
    public URL find(String relativePath) throws Exception {
        if (locationDebug != null) {
            File file = new File(locationDebug, relativePath);

            if (file.exists()) {
                return file.toURL();
            }
        }

        return ResourceUtil.getResource(classLoader, location + relativePath);
    }
}