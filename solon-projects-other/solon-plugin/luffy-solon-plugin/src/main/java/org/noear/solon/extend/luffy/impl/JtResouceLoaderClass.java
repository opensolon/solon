package org.noear.solon.extend.luffy.impl;

import org.noear.luffy.model.AFileModel;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class JtResouceLoaderClass implements JtResouceLoader {

    private final Map<String, AFileModel> fileCached = new LinkedHashMap<>();

    @Override
    public AFileModel fileGet(String path) throws Exception {
        AFileModel file = fileCached.get(path);

        if (file == null) {
            synchronized (fileCached) {
                file = fileCached.get(path);

                if (file == null) {
                    file = new AFileModel();

                    file.content = fileContentGet(path);
                    if (Utils.isNotEmpty(file.content)) {
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

                    fileCached.put(path, file);
                }
            }
        }

        return file;
    }

    protected String fileContentGet(String path) throws IOException {
        return ResourceUtil.getResourceAsString("luffy/" + path, Solon.encoding());
    }
}
