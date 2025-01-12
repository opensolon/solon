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
import org.noear.solon.core.util.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 函数加载器 -外部文件实现
 *
 * @author noear
 * @since 1.3
 */
public class JtFunctionLoaderFile implements JtFunctionLoader {
    static final Logger log = LoggerFactory.getLogger(JtFunctionLoaderFile.class);

    private final Map<String, AFileModel> fileCached = new LinkedHashMap<>();
    private final ReentrantLock SYNC_LOCK = new ReentrantLock();

    private File _baseDir;

    public JtFunctionLoaderFile() {
        this("./luffy/");
    }

    public JtFunctionLoaderFile(String baseDir) {
        _baseDir = new File(baseDir);
        if (!_baseDir.exists()) {
            _baseDir.mkdir();
        }
    }

    @Override
    public AFileModel fileGet(String path) throws Exception {
        File file = fileBuildDo(path);

        if (file == null) {
            return null;
        }

        AFileModel fileModel = fileCached.get(path);

        if (fileModel == null) {
            SYNC_LOCK.lock();
            try {
                fileModel = fileCached.get(path);

                if (fileModel == null) {
                    fileModel = new AFileModel();
                    fileFillDo(fileModel, file, path);

                    fileCached.put(path, fileModel);
                }
            } finally {
                SYNC_LOCK.unlock();
            }
        }

        if (fileModel.update_fulltime.getTime() != file.lastModified()) {
            SYNC_LOCK.lock();
            try {
                if (fileModel.update_fulltime.getTime() != file.lastModified()) {
                    fileFillDo(fileModel, file, path);
                }

                if (fileModel.content != null) {
                    //如果有更新，移除缓存
                    JtRun.dele(path);
                }
            } finally {
                SYNC_LOCK.unlock();
            }
        }

        return fileModel;
    }

    protected void fileFillDo(AFileModel fileModel, File file, String path) throws Exception {
        fileModel.content = fileContentGet(file);

        if (fileModel.content != null) {
            //如果有找到文件内容，则完善信息
            //
            String fileName = file.getName();

            if (fileModel.file_id == 0) {
                //如果还没有 id 说明是第一次加载
                fileModel.path = path;
                fileModel.tag = "luffy";

                if (fileName.indexOf('.') > 0) {
                    String suffix = fileName.substring(fileName.indexOf('.') + 1);
                    fileModel.edit_mode = JtMapping.getActuator(suffix);
                } else {
                    fileModel.edit_mode = JtMapping.getActuator("");
                }

                //有些场景需要id
                fileModel.file_id = Math.abs(path.hashCode());
            }

            fileModel.update_fulltime = new Date(file.lastModified());
        }
    }

    protected File fileBuildDo(String path) {
        if (_baseDir == null) {
            return null;
        } else {
            File file = new File(_baseDir, path);

            if (file.exists()) {
                return file;
            } else {
                return null;
            }
        }
    }

    protected String fileContentGet(File file) {
        try {
            try (InputStream ins = new FileInputStream(file)) {
                return IoUtil.transferToString(ins, Solon.encoding());
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }
}
