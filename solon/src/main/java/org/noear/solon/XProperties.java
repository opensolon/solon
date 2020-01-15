package org.noear.solon;

import org.noear.solon.core.XMap;
import org.noear.solon.core.XPluginEntity;
import org.noear.solon.core.XScaner;
import org.noear.solon.ext.Act2;
import org.noear.solon.ext.Fun1;

import java.net.URL;
import java.util.*;

/**
 * 统一配置加载器
 * */
public final class XProperties extends Properties {
    public static final String server_port = "server.port";

    private XMap _args;
    private List<XPluginEntity> _plugs = new ArrayList<>();

    public XProperties(){
        super();
    }

    public XProperties load(XMap args) {
        _args = args;

        do_loadFile();

        _args.forEach((k, v) -> {
            if (k.indexOf(".") >= 0) {
                setProperty(k, v);
            }
        });

        if (isDebugMode()) {
            System.setProperty("debug", "1");
        }

        return this;
    }

    public XProperties load(URL url) {
        if(url != null) {
            Properties prop = XUtil.getProperties(url);

            if (prop != null) {
                putAll(prop);
            }
        }

        return this;
    }

    protected void plugsScan() {
        //3.查找插件配置（如果出错，让它抛出异常）
        XScaner.scan("solonplugin", n -> n.endsWith(".properties") || n.endsWith(".yml"))
                .stream()
                .map(k -> XUtil.getResource(k))
                .forEach(url -> do_loadPlug(url));

        if (_plugs != null) {
            _plugs.sort(Comparator.comparingInt(p1 -> p1.priority));
        }
    }

    private void do_loadFile() {
        //1.加载文件的配置
        load(XUtil.getResource("application.properties"));
        load(XUtil.getResource("application.yml"));

        Properties sys_prop = System.getProperties();

        Map<String,Object> _tmp = new HashMap<>();

        //2.同步到 System Properties，让别的框架可以从 System 获取
        this.forEach((k,v)->{
            String key = k.toString();
            if(key.indexOf(".") > 0){
                if(sys_prop.containsKey(k)==false){
                    sys_prop.put(k,v);
                    _tmp.put(key,k);
                }
            }
        });


        //2.同步 System 的配置
        sys_prop.forEach((k, v) -> {
            String key = k.toString();

            if(_tmp.containsKey(key)){
                return;
            }

            if ( key.startsWith("solon.") || key.indexOf("server.") >= 0) {
                setProperty(key, String.valueOf(v));
            }
        });
    }

    private void do_loadPlug(URL url){
        try {
            XProperties p = new XProperties().load(url);

            String temp = p.get("solon.plugin");

            if (XUtil.isEmpty(temp) == false) {
                XPluginEntity ent = new XPluginEntity();
                ent.className = temp;
                ent.priority = - p.getInt("solon.plugin.priority", 0);

                _plugs.add(ent);
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private Set<Act2<String,String>> _changeEvent = new HashSet<>();

    public void onChange(Act2<String,String> event){
        _changeEvent.add(event);
    }

    @Override
    public synchronized Object setProperty(String key, String value) {
        Object obj = super.setProperty(key, value);

        _changeEvent.forEach(event->{
            event.run(key,value);
        });

        return obj;
    }

    /**获取启动参数*/
    public XMap argx() {
        return _args;
    }

    /**获取插件列表*/
    public List<XPluginEntity> plugs(){
        return _plugs;
    }



    /**获取服务端口(默认:8080)*/
    public int serverPort() {
        return getInt(server_port, 8080);
    }

    /** 是否为debug mode */
    public boolean isDebugMode(){
        return argx().getInt("debug") == 1;
    }

    //
    //
    //

    /**获取某项配置*/
    public String get(String key) {
        return getProperty(key);
    }
    public String get(String key, String def) {
        return getProperty(key, def);
    }

    public boolean getBool(String key, boolean def){
        return getOrDef(key, def, Boolean::parseBoolean);
    }

    public int getInt(String key, int def) {
        return getOrDef(key, def, Integer::parseInt);
    }

    public long getLong(String key, long def) {
        return getOrDef(key, def, Long::parseLong);
    }

    public Double getDouble(String key, double def) {
        return getOrDef(key, def, Double::parseDouble);
    }

    private <T> T getOrDef(String key, T def, Fun1<String,T> convert){
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return def;
        } else {
            return convert.run(temp);
        }
    }

    public Object getRaw(String key){
        return super.get(key);
    }

    public Properties getProp(String key) {
        Properties prop = new Properties();
        find(key, prop::put);
        return prop;
    }

    public XMap getXmap(String key) {
        XMap map = new XMap();
        find(key,map::put);
        return map;
    }

    private void find(String key, Act2<String,String> setFun){
        String key2 = key + ".";
        int idx2 = key2.length();

        String keyStr = null;
        for (Map.Entry<Object, Object> kv : this.entrySet()) {
            keyStr = kv.getKey().toString();
            if (keyStr.startsWith(key2)) {
                setFun.run(keyStr.substring(idx2), kv.getValue().toString());
            }
        }
    }
}
