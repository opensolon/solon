package org.noear.solon.core;

import org.noear.solon.XProperties;

import java.net.URL;
import java.util.Properties;

public class XPropertiesLoader {
    public static XPropertiesLoader global = new XPropertiesLoader();

    public Properties load(URL url) throws Exception {
        if (url == null) {
            return null;
        }

        String fileName = url.toString();

        if (fileName.endsWith(".properties")) {
            Properties tmp = new XProperties();
            tmp.load(url.openStream());
            return tmp;
        }

        return null;
    }
}
