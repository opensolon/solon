package org.noear.solon.extend.properties.yaml;

import java.io.StringReader;
import java.net.URL;
import java.util.Properties;

public class PropertiesLoader extends org.noear.solon.core.PropertiesLoader {

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
    public Properties build(String text) throws Exception {
        text = text.trim();

        int idx1 = text.indexOf("=");
        int idx2 = text.indexOf(":");

        //有{开头
        if(text.startsWith("{") && text.endsWith("}")){
            PropertiesJson tmp  =new PropertiesJson();
            tmp.loadJson(text);
            return tmp;
        }

        //有[开头
        if(text.startsWith("[") && text.endsWith("]")){
            PropertiesJson tmp  =new PropertiesJson();
            tmp.loadJson(text);
            return tmp;
        }



        //有=
        if (idx1 > 0 && (idx1 < idx2 || idx2 < 0)) {
            Properties tmp = new Properties();
            tmp.load(new StringReader(text));
            return tmp;
        }

        //有:
        if (idx2 > 0 && (idx2 < idx1 || idx1 < 0)) {
            PropertiesYaml tmp = new PropertiesYaml();
            tmp.loadYml(new StringReader(text));
            return tmp;
        }

        return new Properties();
    }
}
