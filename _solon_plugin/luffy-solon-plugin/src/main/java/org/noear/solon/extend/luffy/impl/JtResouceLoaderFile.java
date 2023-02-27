package org.noear.solon.extend.luffy.impl;

import org.noear.luffy.model.AFileModel;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ResourceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

/**
 * @author noear
 * @since 1.3
 */
public class JtResouceLoaderFile implements JtResouceLoader {
    private String _baseUri = "/luffy/";
    private File _baseDir;

    public JtResouceLoaderFile() {
        String rootdir = ResourceUtil.getResource("/").toString().replace("target/classes/", "");

        if (rootdir.startsWith("file:")) {
            String dir_str = rootdir + "src/main/resources" + _baseUri;
            _baseDir = new File(URI.create(dir_str));
            if (!_baseDir.exists()) {
                dir_str = rootdir + "src/main/webapp" + _baseUri;
                _baseDir = new File(URI.create(dir_str));
            }
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
                    return Utils.transferToString(new FileInputStream(file), Solon.encoding());
                } catch (IOException e) {
                    EventBus.pushTry(e);
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}
