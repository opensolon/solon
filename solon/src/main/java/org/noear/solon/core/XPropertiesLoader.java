package org.noear.solon.core;

import org.noear.solon.XUtil;

import java.io.StringReader;
import java.net.URL;
import java.util.Properties;

/**
 * 属性加载器
 *
 * 通过 global 进行重写
 * */
public class XPropertiesLoader {
    public static XPropertiesLoader global = new XPropertiesLoader();

    static {
        //默认的扩展加载器
        //
        String loader = "org.noear.solon.extend.properties.yaml.PropertiesLoader";

        XPropertiesLoader tmp = XUtil.newInstance(loader);
        if (tmp != null) {
            global = tmp;
        }
    }

    /**
     * 是否支持
     *
     * @param suffix 文件后缀
     * */
    public boolean isSupport(String suffix) {
        if (suffix == null) {
            return false;
        }

        return suffix.endsWith(".properties");
    }


    /**
     * 加载 url 配置
     * */
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

        return null;
    }

    /**
     * 构建 txt 配置
     * */
    public Properties build(String txt) throws Exception {
        int idx1 = txt.indexOf("=");
        int idx2 = txt.indexOf(":");

        if (idx1 > 0 && (idx1 < idx2 || idx2 < 0)) {
            Properties tmp = new Properties();
            tmp.load(new StringReader(txt));
            return tmp;
        }

        return new Properties();
    }
}
