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

import org.noear.solon.core.ExtendLoader;
import org.noear.solon.web.staticfiles.StaticRepository;

import java.io.File;
import java.net.URL;

/**
 * 扩展目录型静态仓库
 *
 * @author noear
 * @since 1.5
 */
public class ExtendStaticRepository implements StaticRepository {
    private File location;

    public ExtendStaticRepository() {
        File folder = ExtendLoader.folder();
        if (folder == null) {
            throw new IllegalStateException("No extension directory exists");
        }

        location = new File(folder, "static");
    }

    /**
     * @param relativePath 例：demo/file.htm （没有'/'开头）
     */
    @Override
    public URL find(String relativePath) throws Exception {
        File file = new File(location, relativePath);

        if (file.exists()) {
            return file.toURI().toURL();
        } else {
            return null;
        }
    }
}
