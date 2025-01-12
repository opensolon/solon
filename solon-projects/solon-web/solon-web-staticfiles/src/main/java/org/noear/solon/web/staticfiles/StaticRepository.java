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
package org.noear.solon.web.staticfiles;

import org.noear.solon.core.util.IoUtil;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 静态仓库
 *
 * @author noear
 * @since 1.5
 */
public interface StaticRepository {
    /**
     * 查找
     *
     * @param relativePath 例：demo/file.htm （没有'/'开头）
     */
    URL find(String relativePath) throws Exception;

    /**
     * 预热
     *
     * @param relativePath 例：demo/file.htm （没有'/'开头）
     */
    default void preheat(String relativePath, boolean useCaches) throws Exception {
        URL url = find(relativePath);

        if (url != null) {
            URLConnection connection = url.openConnection();
            connection.setUseCaches(useCaches);
            try (InputStream stream = connection.getInputStream()) {
                IoUtil.transferToString(stream);
            }
        }
    }
}
