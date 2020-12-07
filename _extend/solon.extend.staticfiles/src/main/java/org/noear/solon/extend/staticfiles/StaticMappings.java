package org.noear.solon.extend.staticfiles;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

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
}
