package org.noear.solon.extend.staticfiles.repository;

import org.noear.solon.extend.staticfiles.StaticRepository;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * @author noear
 * @since 1.5
 */
public class FileStaticRepository implements StaticRepository {
    String dir;

    public FileStaticRepository(String dir) {
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
