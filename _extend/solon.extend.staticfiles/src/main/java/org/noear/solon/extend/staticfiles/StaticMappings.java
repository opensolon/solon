package org.noear.solon.extend.staticfiles;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class StaticMappings extends ArrayList<StaticMapping> {
    private StaticMappings() {
        super();
    }

    private static StaticMappings _instance;

    public static StaticMappings instance() {
        if (_instance == null) {
            _instance = new StaticMappings();
        }

        return _instance;
    }


    public StaticMappings add(String path, String location) {
        StaticMapping mapping = new StaticMapping();
        mapping.path = path;
        mapping.location = location;

        if (Solon.cfg().isDebugMode()) {
            String dirroot = Utils.getResource("/")
                    .toString()
                    .replace("target/classes/", "");

            if (dirroot.startsWith("file:")) {
                mapping.locationDebug = dirroot + "src/main/resources" + location;
            }
        }

        this.add(mapping);

        return this;
    }

    protected URL find(String path) throws Exception {
        URL rst = null;

        for (StaticMapping m : this) {
            if (path.startsWith(m.path)) {
                if (m.locationDebug != null) {
                    URI uri = URI.create(m.locationDebug + path);
                    File file = new File(uri);

                    if (file.exists()) {
                        rst = uri.toURL();
                    }
                } else {
                    rst = Utils.getResource(m.location + path);
                }

                if (rst != null) {
                    return rst;
                }
            }
        }

        return null;
    }
}
