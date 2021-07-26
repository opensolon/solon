package org.noear.solon.extend.staticfiles;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class StaticMappings extends ArrayList<StaticLocation> {
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


    public StaticMappings add(String start, String location) {

        // 去掉头尾的 "/"

        if (location.endsWith("/")) {
            location = location.substring(0, location.length() - 1);
        }

        if(location.startsWith("/")){
            location = location.substring(1);
        }

        StaticLocation mapping = new StaticLocation();
        mapping.start = start;
        mapping.location = location;

        if (Solon.cfg().isDebugMode()) {
            URL rooturi = Utils.getResource("/");

            if (rooturi != null) {
                String rootdir = rooturi.toString()
                        .replace("target/classes/", "");

                if (rootdir.startsWith("file:")) {
                    mapping.locationDebug = rootdir + "src/main/resources/" + location;
                }
            }
        }

        this.add(mapping);

        return this;
    }

    protected URL find(String path) throws Exception {
        URL rst = null;

        for (StaticLocation m : this) {
            if (path.startsWith(m.start)) {
                if (m.locationDebug != null) {
                    URI uri = URI.create(m.locationDebug + path);
                    File file = new File(uri);

                    if (file.exists()) {
                        rst = uri.toURL();
                    }
                }

                if (rst == null) {
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
