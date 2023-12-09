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

/**
 * 函数加载器 -外部文件实现
 *
 * @author noear
 * @since 1.3
 */
public class JtFunctionLoaderFile implements JtFunctionLoader {
    static final Logger log = LoggerFactory.getLogger(JtFunctionLoaderFile.class);

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
        }

        return file;
    }

    protected String fileContentGet(String path) {
        if (_baseDir == null) {
            return null;
        } else {
            File file = new File(_baseDir, path);

            if (file.exists()) {
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
