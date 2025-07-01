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
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 函数加载器 - 资源目录实现
 *
 * @author noear
 * @since 1.3
 */
public class JtFunctionLoaderClasspath implements JtFunctionLoader {

    private final Map<String, AFileModel> fileCached = new LinkedHashMap<>();
    private final ReentrantLock SYNC_LOCK = new ReentrantLock();

    @Override
    public AFileModel fileGet(String path) throws Exception {
        AFileModel file = fileCached.get(path);

        if (file == null) {
            SYNC_LOCK.lock();
            try {
                file = fileCached.get(path);

                if (file == null) {
                    file = fileGet0(path);

                    fileCached.put(path, file);
                }
            } finally {
                SYNC_LOCK.unlock();
            }
        }

        return file;
    }

    protected AFileModel fileGet0(String path) throws Exception {
        AFileModel fileModel = new AFileModel();

        fileModel.content = fileContentGet(path);

        if (Utils.isNotEmpty(fileModel.content)) {
            //如果有找到文件内容，则完善信息
            JtModelUtils.buildFileModel(fileModel, path);
        }

        return fileModel;
    }

    protected String fileContentGet(String path) throws IOException {
        URL url = ResourceUtil.getResource("luffy/" + path);

        if (url == null) {
            return null;
        }

        File file = new File(url.getFile());

        if (file.exists() && file.isFile()) {
            try (InputStream in = url.openStream()) {
                return IoUtil.transferToString(in, Solon.encoding());
            }
        } else {
            return null;
        }
    }
}
