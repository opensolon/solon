package org.noear.solon;

import org.noear.solon.core.XMap;
import org.noear.solon.core.XPluginEntity;
import org.noear.solon.core.XProperties;
import org.noear.solon.core.util.ResourceScaner;

import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * 统一配置加载器
 *
 * <pre><code>
 * //
 * // 手动获取配置模式（容器自动模式可用: @XInject("${water.logger}")）
 * //
 * XApp.cfg()
 * XApp.cfg().isDebugMode()
 * XApp.cfg().isDriftMode()
 * XApp.cfg().get("water.logger")
 * XApp.cfg().getProp("db1")
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
public final class XAppProperties extends XProperties {
    private XMap args;
    private List<XPluginEntity> plugs = new ArrayList<>();
    private boolean isDebugMode;
    private boolean isDriftMode;
    private boolean isFilesMode;
    private String  extend;

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
        this.args = args;

        //2.加载文件的配置
        loadAdd(XUtil.getResource("application.properties"));
        loadAdd(XUtil.getResource("application.yml"));

        //3.同步启动参数
        this.args.forEach((k, v) -> {
            if (k.indexOf(".") >= 0) {
                this.setProperty(k, v);
                System.setProperty(k, v);
            }
        });

        isDebugMode = argx().getInt("debug") == 1;
        isDriftMode = argx().getInt("drift") == 1;
        isFilesMode = "file".equals(this.getClass().getProtectionDomain().getCodeSource().getLocation().getProtocol());

        //4.标识debug模式
        if (isDebugMode()) {
            System.setProperty("debug", "1");
        }

        //5.扩展文件夹
        extend = this.args.get("extend");
        if (XUtil.isEmpty(extend)) {
            extend = get("solon.extend");
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
        ResourceScaner.scan("solonplugin", n -> n.endsWith(".properties") || n.endsWith(".yml"))
                .stream()
                .map(k -> XUtil.getResource(k))
                .forEach(url -> plugsScanMapDo(url));

        ResourceScaner.scan("META-INF/solon", n -> n.endsWith(".properties") || n.endsWith(".yml"))
                .stream()
                .map(k -> XUtil.getResource(k))
                .forEach(url -> plugsScanMapDo(url));

        if (plugs.size() > 0) {
            //进行优先级顺排（数值要倒排）
            //
            plugs.sort(Comparator.comparingInt(XPluginEntity::getPriority).reversed());
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
                ent.clzName = temp;
                ent.priority = p.getInt("solon.plugin.priority", 0);

                plugs.add(ent);
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
        return args;
    }

    /**
     * 获取插件列表
     */
    public List<XPluginEntity> plugs() {
        return plugs;
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
        return extend;
    }

    /**
     * 框架版本号
     * */
    public String version(){
        return "1.1.6";
    }

    /**
     * 是否为 debug mode
     */
    public boolean isDebugMode() {
        return isDebugMode;
    }

    /**
     * 是否为文件运行模式
     * */
    public boolean isFilesMode(){
        return isFilesMode;
    }

    /**
     * 是否为 drift mode (of ip)
     * */
    public boolean isDriftMode() {
        return isDriftMode;
    }
}
