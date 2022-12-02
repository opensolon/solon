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
    String location;

    public ExtendStaticRepository() {
        String path = ExtendLoader.path();
        if (path == null) {
            throw new IllegalStateException("No extension directory exists");
        }

        location = (path + "static");
    }

    /**
     * @param relativePath 例：demo/file.htm （没有'/'开头）
     * */
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
