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
package org.noear.solon.luffy.impl;

import org.noear.luffy.model.AFileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 2.6
 */
public class JtFunctionLoaderManager implements JtFunctionLoader {
    private final List<JtFunctionLoader> resourceLoaders = new ArrayList<>();

    /**
     * 添加
     */
    public void add(int index, JtFunctionLoader resourceLoader) {
        resourceLoaders.add(index, resourceLoader);
    }

    /**
     * 清空
     */
    public void clear() {
        resourceLoaders.clear();
    }

    @Override
    public AFileModel fileGet(String path) throws Exception {
        AFileModel aFile = null;
        for (JtFunctionLoader rl : resourceLoaders) {
            aFile = rl.fileGet(path);

            if (aFile != null && aFile.content != null) {
                return aFile;
            }
        }
        return null;
    }
}
