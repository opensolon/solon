package org.noear.solon.extend.staticfiles.repository;

import org.noear.solon.extend.staticfiles.StaticRepository;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * 文件型静态仓库
 *
 * @author noear
 * @since 1.5
 */
public class FileStaticRepository implements StaticRepository {
    String dir;

    /**
     * @param dir 例：/user/
     * */
    public FileStaticRepository(String dir) {
        if (dir.endsWith("/")) {
            dir = dir.substring(0, dir.length() - 1);
        }

        if (dir.startsWith("file://") == false) {
            dir = "file://" + dir;
        }

        this.dir = dir;
    }

    @Override
    public URL find(String path) throws Exception {
        URI uri = URI.create(dir + path);
        File file = new File(uri);

        if (file.exists()) {
            return uri.toURL();
        } else {
            return null;
        }
    }
}
