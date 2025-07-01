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
import org.noear.snack.ONode;
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
    static final String debug_dir = "/luffy/";

    static JtFunctionLoaderFile ofDebug() {
        //调试模式（直接连接源码资源目录）
        File dir = DebugUtils.getDebugLocation(AppClassLoader.global(), debug_dir);
        return new JtFunctionLoaderFile(dir, false);
    }

    private final Map<String, AFileModel> fileCached = new LinkedHashMap<>();
    private final ReentrantLock SYNC_LOCK = new ReentrantLock();

    private File _baseDir;
    private boolean _useCache = true;

    public JtFunctionLoaderFile() {
        this("./luffy/");
    }

    public JtFunctionLoaderFile(String baseDir) {
        _baseDir = new File(baseDir);
        if (!_baseDir.exists()) {
            _baseDir.mkdir();
        }
    }

    public JtFunctionLoaderFile(File baseDir, boolean useCache) {
        _baseDir = baseDir;
        _useCache = useCache;
    }

    @Override
    public AFileModel fileGet(String path) throws Exception {
        if (path.contains("../") || path.contains("..\\")) {
            // '../','..\' 不安全，禁止进入资料库
            return null;
        }

        File file = fileBuildDo(path);

        if (file == null) {
            return null;
        }

        if (_useCache == false) {
            return fileFillDo(new AFileModel(), file, path);
        } else {

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
    }

    protected AFileModel fileFillDo(AFileModel fileModel, File file, String path) throws Exception {
        fileModel.content = fileContentGet(file);

        if (fileModel.content != null) {
            //如果有找到文件内容，则完善信息
            if (fileModel.file_id == 0) {
                JtModelUtils.buildFileModel(fileModel, path);
            }

            fileModel.update_fulltime = new Date(file.lastModified());
        }

        return fileModel;
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