package org.noear.solonboot;

import java.net.URL;
import java.util.Properties;
import java.util.function.BiConsumer;

public final class XProperties {
    public static final String server_port = "server.port";

    private Properties _properties;
    private XMap _args;

    public XProperties(String[] args) {
        _args = XMap.from(args);

        tryLoad();

        _args.forEach((k, v) -> {
            if (k.indexOf("server.") >= 0) {
                _properties.setProperty(k, v);
            }
        });
    }

    private void tryLoad() {
        if (_properties == null) {
            _properties = new Properties();

            //加载文件的配置
            do_load("solonboot.http.properties");
            do_load("solonboot.rpcx.properties");
            do_load("application.properties");

            //再加载代码的配置
            System.getProperties().forEach((k, v) -> {
                String key = k.toString();

                if (key.indexOf("server.") >= 0) {
                    _properties.setProperty(key, String.valueOf(v));
                }
            });
        }
    }

    private void do_load(String fileName) {
        try {
            URL temp = XUtil.getResource(fileName);
            if (temp != null) {
                _properties.load(temp.openStream());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public XMap argx() {
        return _args;
    }

    //获取配置属性对象
    public Properties props() {
        return _properties;
    }

    public void forEach(BiConsumer<Object, Object> action) {
        props().forEach(action);
    }

    //获取配置属性
    public String get(String key) {
        return props().getProperty(key);
    }

    public String get(String key, String def) {
        return props().getProperty(key, def);
    }

    public int getInt(String key, int def) {
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return def;
        } else {
            return Integer.parseInt(temp);
        }
    }

    public long getLong(String key, long def) {
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return def;
        } else {
            return Long.parseLong(temp);
        }
    }

    public int serverPort() {
        return getInt(server_port, 8080);
    }
}
