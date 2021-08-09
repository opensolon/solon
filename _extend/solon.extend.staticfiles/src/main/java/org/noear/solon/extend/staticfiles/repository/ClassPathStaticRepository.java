package org.noear.solon.extend.staticfiles.repository;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.extend.staticfiles.StaticRepository;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * @author noear
 * @since 1.5
 */
public class ClassPathStaticRepository implements StaticRepository {
    String location;
    String locationDebug;

    /**
     * @param location 例：static
     * */
    public ClassPathStaticRepository(String location) {
        // 去掉头尾的 "/"
        if (location.endsWith("/")) {
            location = location.substring(0, location.length() - 1);
        }

        if (location.startsWith("/")) {
            location = location.substring(1);
        }

        this.location = location;

        if (Solon.cfg().isDebugMode()) {
            URL rooturi = Utils.getResource("/");

            if (rooturi != null) {
                String rootdir = rooturi.toString()
                        .replace("target/classes/", "");

                if (rootdir.startsWith("file:")) {
                    this.locationDebug = rootdir + "src/main/resources/" + location;
                }
            }
        }
    }

    @Override
    public URL find(String path) throws Exception {
        if (locationDebug != null) {
            URI uri = URI.create(locationDebug + path);
            File file = new File(uri);

            if (file.exists()) {
                return uri.toURL();
            }
        }

        return Utils.getResource(location + path);
    }
}
