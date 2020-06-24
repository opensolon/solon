package org.noear.solon.extend.properties.yaml;

import org.noear.solon.core.XPropertiesLoader;

import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Properties;

public class PropertiesLoader extends XPropertiesLoader {

    public static final PropertiesLoader g = new PropertiesLoader();


    @Override
    public boolean isSupport(String suffix) {
        if (suffix == null) {
            return false;
        }

        return suffix.endsWith(".properties") || suffix.endsWith(".yml");
    }

    @Override
    public Properties load(URL url) throws Exception {
        if (url == null) {
            return null;
        }

        String fileName = url.toString();

        if (fileName.endsWith(".properties")) {
            System.out.println(url);

            Properties tmp = new Properties();
            tmp.load(url.openStream());
            return tmp;
        }

        if (fileName.endsWith(".yml")) {
            System.out.println(url);

            PropertiesYaml tmp = new PropertiesYaml();
            tmp.loadYml(url.openStream());
            return tmp;
        }

        return null;
    }

    @Override
    public Properties load(String text) throws Exception {
        int idx1 = text.indexOf("=");
        int idx2 = text.indexOf(":");

        if (idx1 > 0 && (idx1 < idx2 || idx2 < 0)) {
            Properties tmp = new Properties();
            tmp.load(new StringReader(text));
            return tmp;
        }

        if (idx2 > 0 && (idx2 < idx1 || idx1 < 0)) {
            PropertiesYaml tmp = new PropertiesYaml();
            tmp.load(new StringReader(text));
            return tmp;
        }

        return null;
    }
}
