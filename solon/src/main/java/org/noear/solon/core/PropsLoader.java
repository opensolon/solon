package org.noear.solon.core;

import org.noear.solon.Utils;
import org.noear.solon.core.util.PrintUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Properties;

/**
 * 属性加载器
 *
 * 通过 globalSet 可进行重写
 *
 * @see Utils#loadProperties(URL)
 * @author noear
 * @since 1.0
 * */
public class PropsLoader {
    private static PropsLoader global;
    public static PropsLoader global() {
        return global;
    }

    public static void globalSet(PropsLoader instance) {
        if(instance != null) {
            PropsLoader.global = instance;
        }
    }

    static {
        //默认的扩展加载器
        //
        PropsLoader tmp = Utils.newInstance("org.noear.solon.extend.impl.PropsLoaderExt");

        if (tmp == null) {
            global = new PropsLoader();
        } else {
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

        return null;
    }

    /**
     * 构建 txt 配置
     * */
    public Properties build(String txt) throws IOException {
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
