package org.noear.solon.core;

import org.noear.solon.XProperties;
import org.noear.solon.XUtil;

import java.net.URL;
import java.util.Properties;

public class XPropertiesLoader {
    public static XPropertiesLoader global = new XPropertiesLoader();

    static {
        String loader = "org.noear.solon.extend.properties.yaml.PropertiesLoader";

        Class<?> clz = XUtil.loadClass(loader);
        if (clz != null) {
            try {
                Object tmp = clz.newInstance();
                global = (XPropertiesLoader) tmp;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean isSupport(String suffix) {
        if (suffix == null) {
            return false;
        }

        return suffix.endsWith(".properties");
    }

    public Properties load(URL url) throws Exception {
        if (url == null) {
            return null;
        }

        String fileName = url.toString();

        if (fileName.endsWith(".properties")) {
            System.out.println(url);

            Properties tmp = new XProperties();
            tmp.load(url.openStream());
            return tmp;
        }

        return null;
    }
}
