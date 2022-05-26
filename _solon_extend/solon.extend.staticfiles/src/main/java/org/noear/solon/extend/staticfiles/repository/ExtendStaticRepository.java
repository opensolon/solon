package org.noear.solon.extend.staticfiles.repository;

import org.noear.solon.core.ExtendLoader;
import org.noear.solon.extend.staticfiles.StaticRepository;

import java.io.File;
import java.net.URL;

/**
 * 扩展目录型静态仓库
 *
 * @author noear
 * @since 1.5
 */
public class ExtendStaticRepository implements StaticRepository {
    String location;

    public ExtendStaticRepository() {
        String path = ExtendLoader.path();
        if (path == null) {
            throw new RuntimeException("No extension directory exists");
        }

        location = (path + "static");
    }

    @Override
    public URL find(String path) throws Exception {
        File file = new File(location, path);

        if (file.exists()) {
            return file.toURI().toURL();
        } else {
            return null;
        }
    }
}
