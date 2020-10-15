package org.noear.solon;

import org.noear.solon.core.XMap;
import org.noear.solon.core.XPluginEntity;
import org.noear.solon.core.XProperties;
import org.noear.solon.core.XScaner;

import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * 统一配置加载器
 * */
public final class XAppProperties extends XProperties {
    private XMap _args;
    private List<XPluginEntity> _plugs = new ArrayList<>();
    private boolean _isDebugMode;
    private boolean _isDriftMode;
    private boolean _isFilesMode;
    private String _extend;

    public XAppProperties() {
        super(System.getProperties());
    }

    /**
     * 加载配置（用于第一次加载）
     *
     * @param args 启用参数
     * */
    public XAppProperties load(XMap args) {
        //1.接收启动参数
        _args = args;

        //2.加载文件的配置
        loadAdd(XUtil.getResource("application.properties"));
        loadAdd(XUtil.getResource("application.yml"));

        //3.同步启动参数
        _args.forEach((k, v) -> {
            if (k.indexOf(".") >= 0) {
                this.setProperty(k, v);
                System.setProperty(k, v);
            }
        });

        _isDebugMode = argx().getInt("debug") == 1;
        _isDriftMode = argx().getInt("drift") == 1;
        _isFilesMode = "file".equals(this.getClass().getProtectionDomain().getCodeSource().getLocation().getProtocol());

        //4.标识debug模式
        if (isDebugMode()) {
            System.setProperty("debug", "1");
        }

        //5.扩展文件夹
        _extend = _args.get("extend");
        if (XUtil.isEmpty(_extend)) {
            _extend = get("solon.extend");
        }

        return this;
    }

    /**
     * 加载配置（用于扩展加载）
     *
     * @param url 配置地址
     * */
    public XAppProperties loadAdd(URL url) {
        if (url != null) {
            Properties prop = XUtil.loadProperties(url);

            if (prop != null) {
                putAll(prop);
                System.getProperties().putAll(prop);
            }
        }

        return this;
    }

    public XAppProperties loadAdd(String url) {
        return loadAdd(XUtil.getResource(url));
    }



    /**
     * 插件扫描
     * */
    protected void plugsScan() {
        //3.查找插件配置（如果出错，让它抛出异常）
        XScaner.scan("solonplugin", n -> n.endsWith(".properties") || n.endsWith(".yml"))
                .stream()
                .map(k -> XUtil.getResource(k))
                .forEach(url -> plugsScanMapDo(url));

        XScaner.scan("META-INF/solon", n -> n.endsWith(".properties") || n.endsWith(".yml"))
                .stream()
                .map(k -> XUtil.getResource(k))
                .forEach(url -> plugsScanMapDo(url));

        if (_plugs.size() > 0) {
            //进行优先级顺排（数值要倒排）
            //
            _plugs.sort(Comparator.comparingInt(XPluginEntity::getPriority).reversed());
        }
    }

    /**
     * 插件扫描，根据某个资源地址扫描
     *
     * @param url 资源地址
     * */
    private void plugsScanMapDo(URL url) {
        try {
            XProperties p = new XProperties(XUtil.loadProperties(url));

            String temp = p.get("solon.plugin");

            if (XUtil.isEmpty(temp) == false) {
                XPluginEntity ent = new XPluginEntity();
                ent.className = temp;
                ent.priority = p.getInt("solon.plugin.priority", 0);

                _plugs.add(ent);
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private Set<BiConsumer<String, String>> _changeEvent = new HashSet<>();

    /**
     * 添加变更事件
     * */
    public void onChange(BiConsumer<String, String> event) {
        _changeEvent.add(event);
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        Object obj = super.put(key, value);

        if (key instanceof String && value instanceof String) {
            _changeEvent.forEach(event -> {
                event.accept((String) key, (String) value);
            });
        }

        return obj;
    }

    /**
     * 获取启动参数
     */
    public XMap argx() {
        return _args;
    }

    /**
     * 获取插件列表
     */
    public List<XPluginEntity> plugs() {
        return _plugs;
    }


    /**
     * 获取服务端口(默认:8080)
     */
    public int serverPort() {
        return getInt("server.port", 8080);
    }

    /**
     * 包部扩展文件夹
     * */
    public String extend(){
        return _extend;
    }

    /**
     * 框架版本号
     * */
    public String version(){
        return "1.1.3";
    }

    /**
     * 是否为 debug mode
     */
    public boolean isDebugMode() {
        return _isDebugMode;
    }

    /**
     * 是否为文件运行模式
     * */
    public boolean isFilesMode(){
        return _isFilesMode;
    }

    /**
     * 是否为 drift mode (of ip)
     * */
    public boolean isDriftMode() {
        return _isDriftMode;
    }
}
