package org.noear.solon.extend.properties.yaml;

import org.noear.solon.core.PropsLoader;
import org.noear.solon.core.util.PrintUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Properties;

public class PropertiesLoader extends PropsLoader {

    public static final PropertiesLoader g = new PropertiesLoader();


    @Override
    public boolean isSupport(String suffix) {
        if (suffix == null) {
            return false;
        }

        return suffix.endsWith(".properties") || suffix.endsWith(".yml");
    }

    @Override
    public Properties load(URL url) throws IOException {
        if (url == null) {
            return null;
        }

        String fileName = url.toString();

        if (fileName.endsWith(".properties")) {
            PrintUtil.info(url);

            Properties tmp = new Properties();
            tmp.load(new InputStreamReader(url.openStream()));
            return tmp;
        }

        if (fileName.endsWith(".yml")) {
            PrintUtil.info(url);

            PropertiesYaml tmp = new PropertiesYaml();
            tmp.loadYml(new InputStreamReader(url.openStream()));
            return tmp;
        }

        return null;
    }

    @Override
    public Properties build(String text) throws IOException {
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
