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
import org.noear.solon.boot.web.DebugUtils;
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.util.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 函数加载器 - 资源目录实现(调试模式)
 *
 * @author noear
 * @since 1.3
 */
public class JtFunctionLoaderDebug implements JtFunctionLoader {
    static final Logger log = LoggerFactory.getLogger(JtFunctionLoaderDebug.class);

    private String _baseUri = "/luffy/";
    private File _baseDir;

    public JtFunctionLoaderDebug() {
        _baseDir = DebugUtils.getDebugLocation(AppClassLoader.global(), _baseUri);
    }

    @Override
    public AFileModel fileGet(String path) throws Exception {
        AFileModel file = new AFileModel();

        file.content = fileContentGet(path);

        if (file.content != null) {
            //如果有找到文件内容，则完善信息
            //
            File file1 = new File(path);
            String fileName = file1.getName();

            file.path = path;
            file.tag = "luffy";

            if (fileName.indexOf('.') > 0) {
                String suffix = fileName.substring(fileName.indexOf('.') + 1);
                file.edit_mode = JtMapping.getActuator(suffix);
            } else {
                file.edit_mode = JtMapping.getActuator("");
            }

            //有些场景需要id
            file.file_id = Math.abs(path.hashCode());
        }

        return file;
    }

    protected String fileContentGet(String path) {
        if (_baseDir == null) {
            return null;
        } else {
            File file = new File(_baseDir, path);

            if (file.exists() && file.isFile()) {
                try {
                    try (InputStream ins = new FileInputStream(file)) {
                        return IoUtil.transferToString(ins, Solon.encoding());
                    }
                } catch (IOException e) {
                    log.warn(e.getMessage(), e);
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}