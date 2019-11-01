package org.noear.solon.extend.properties.yaml;

import org.noear.solon.core.XPropertiesLoader;

import java.net.URL;
import java.util.Properties;

public class PropertiesLoaderEx extends XPropertiesLoader {

    public static final PropertiesLoaderEx g = new PropertiesLoaderEx();

    @Override
    public Properties load(URL url) throws Exception {
        if (url == null) {
            return null;
        }

        String fileName = url.toString();

        if (fileName.endsWith(".properties")) {
            Properties tmp = new Properties();
            tmp.load(url.openStream());
            return tmp;
        }

        if (fileName.endsWith(".yml")) {
            PropertiesYaml tmp = new PropertiesYaml();
            tmp.loadYml(url.openStream());
            return tmp;
        }

        return null;
    }
}
