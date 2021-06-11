package org.noear.solon;

import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.PluginEntity;
import org.noear.solon.core.Props;
import org.noear.solon.core.util.ResourceScaner;

import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * 统一配置加载器
 *
 * <pre><code>
 * //
 * // 手动获取配置模式（容器自动模式可用: @Inject("${water.logger}")）
 * //
 * Solon.cfg()
 * Solon.cfg().isDebugMode()
 * Solon.cfg().isDriftMode()
 * Solon.cfg().get("water.logger")
 * Solon.cfg().getProp("db1")
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
public final class SolonProps extends Props {
    private NvMap args;
    private Class<?> source;
    private URL sourceLocation;
    private List<PluginEntity> plugs = new ArrayList<>();
    private boolean isDebugMode;
    private boolean isDriftMode;
    private boolean isFilesMode;
    private boolean isWhiteMode;
    private boolean isSetupMode;
    private String extend;
    private String extendFilter;
    private String appName;
    private String appGroup;
    private String appTitle;

    public SolonProps() {
        super(System.getProperties());
    }

    /**
     * 加载配置（用于第一次加载）
     *
     * @param args 启用参数
     */
    public SolonProps load(Class<?> source, NvMap args) {
        //1.接收启动参数
        this.args = args;
        this.source = source;
        this.sourceLocation = source.getProtectionDomain().getCodeSource().getLocation();

        //2.加载文件的配置
        //@Deprecated
        loadInit(Utils.getResource("application.properties"));
        //@Deprecated
        loadInit(Utils.getResource("application.yml"));
        loadInit(Utils.getResource("app.properties"));
        loadInit(Utils.getResource("app.yml"));

        //2.2.加载活动配置
        String env = args.get("env");

        //@Deprecated
        if(Utils.isEmpty(env)){
            //兼容旧版本
            env = args.get("active");
        }

        if(Utils.isEmpty(env)){
            env = get("solon.profiles.active");
        }

        if (Utils.isNotEmpty(env)) {
            //@Deprecated
            loadInit(Utils.getResource("application-" + env + ".properties"));
            //@Deprecated
            loadInit(Utils.getResource("application-" + env + ".yml"));
            loadInit(Utils.getResource("app-" + env + ".properties"));
            loadInit(Utils.getResource("app-" + env + ".yml"));
        }

        //3.同步启动参数
        this.args.forEach((k, v) -> {
            if (k.indexOf(".") >= 0) {
                this.setProperty(k, v);
                System.setProperty(k, v);
            }
        });

        isDebugMode = argx().getInt("debug") == 1;
        isSetupMode = argx().getInt("setup") == 1;
        isWhiteMode = argx().getInt("white", 1) == 1;
        isFilesMode = (sourceLocation.getPath().endsWith(".jar") == false
                        && sourceLocation.getPath().contains(".jar!/") == false
                        && sourceLocation.getPath().endsWith(".zip") == false
                        && sourceLocation.getPath().contains(".zip!/") == false);

        //4.标识debug模式
        if (isDebugMode()) {
            System.setProperty("debug", "1");
        }

        String drift = this.args.get("drift");
        if (Utils.isEmpty(drift)) {
            drift = get("solon.drift");
        }
        isDriftMode = "1".equals(drift);

        //5.扩展文件夹
        extend = this.args.get("extend");
        if (Utils.isEmpty(extend)) {
            extend = get("solon.extend");
        }

        //5.1.扩展文件夹过滤器
        extendFilter = this.args.get("extend.filter");
        if (Utils.isEmpty(extendFilter)) {
            extendFilter = get("solon.extend.filter");
        }

        //6.应用名
        appName = this.args.get("app.name");
        if (Utils.isEmpty(appName)) {
            appName = get("solon.app.name");
        }

        //6.1.应用组
        appGroup = this.args.get("app.group");
        if (Utils.isEmpty(appGroup)) {
            appGroup = get("solon.app.group");
        }

        //6.1.应用标准
        appTitle = this.args.get("app.title");
        if (Utils.isEmpty(appTitle)) {
            appTitle = get("solon.app.title");
        }

        return this;
    }

    /**
     * 加载配置（用于扩展加载）
     *
     * @param url 配置地址
     */
    public SolonProps loadAdd(URL url) {
        if (url != null) {
            Properties props = Utils.loadProperties(url);
            loadAdd(props);
        }

        return this;
    }

    /**
     * 加载配置（用于扩展加载）
     * */
    public SolonProps loadAdd(String url) {
        return loadAdd(Utils.getResource(url));
    }

    /**
     * 加载配置（用于扩展加载）
     * */
    public SolonProps loadAdd(Properties props) {
        if (props != null) {
            for (Map.Entry<Object, Object> kv : props.entrySet()) {
                Object v1 = kv.getValue();
                if (v1 instanceof String) {
                    // db1.url=xxx
                    // db1.jdbcUrl=${db1.url}
                    String tmpV = (String) v1;
                    if (tmpV.startsWith("${") && tmpV.endsWith("}")) {
                        String tmpK = tmpV.substring(2, tmpV.length() - 1);
                        tmpV = props.getProperty(tmpK);
                        if (tmpV == null) {
                            tmpV = getProperty(tmpK);
                        }
                        v1 = tmpV;
                    }
                }

                System.getProperties().put(kv.getKey(), v1);
                put(kv.getKey(), v1);
            }
        }

        return this;
    }

    /**
     * 加载初始化配置
     *
     * 1.优先使用 system properties；可以在启动时修改配置
     * 2.之后同时更新 system properties 和 solon cfg
     * */
    protected void loadInit(URL url) {
        if (url != null) {
            Properties props = Utils.loadProperties(url);

            for (Map.Entry kv : System.getProperties().entrySet()) {
                if (props.containsKey(kv.getKey())) {
                    props.put(kv.getKey(), kv.getValue());
                }
            }

            loadAdd(props);
        }
    }


    /**
     * 插件扫描
     */
    protected void plugsScan(List<ClassLoader> classLoaders) {
        for (ClassLoader classLoader : classLoaders) {
            //3.查找插件配置（如果出错，让它抛出异常）
            ResourceScaner.scan(classLoader, "META-INF/solon", n -> n.endsWith(".properties") || n.endsWith(".yml"))
                    .stream()
                    .map(k -> Utils.getResource(classLoader, k))
                    .forEach(url -> plugsScanMapDo(classLoader, url));
        }

        //扫描主配置
        plugsScanLoadDo(JarClassLoader.global(), this);

        plugsSort();
    }

    /**
     * 插件扫描，根据某个资源地址扫描
     *
     * @param url 资源地址
     */
    private void plugsScanMapDo(ClassLoader classLoader, URL url) {
        Props p = new Props(Utils.loadProperties(url));
        plugsScanLoadDo(classLoader, p);
    }

    private void plugsScanLoadDo(ClassLoader classLoader, Props p) {
        String pluginStr = p.get("solon.plugin");

        if (Utils.isNotEmpty(pluginStr)) {
            int priority = p.getInt("solon.plugin.priority", 0);
            String[] plugins = pluginStr.trim().split(",");

            for (String clzName : plugins) {
                if (clzName.length() > 0) {
                    PluginEntity ent = new PluginEntity(classLoader, clzName.trim());
                    ent.priority = priority;
                    plugs.add(ent);
                }
            }
        }
    }

    private Set<BiConsumer<String, String>> _changeEvent = new HashSet<>();

    /**
     * 添加变更事件
     */
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

    public Class<?> source() {
        return source;
    }

    public URL sourceLocation() {
        return sourceLocation;
    }

    /**
     * 获取启动参数
     */
    public NvMap argx() {
        return args;
    }

    /**
     * 获取插件列表
     */
    public List<PluginEntity> plugs() {
        return plugs;
    }

    /**
     * 插件排序
     * */
    public void plugsSort(){
        if (plugs.size() > 0) {
            //进行优先级顺排（数值要倒排）
            //
            plugs.sort(Comparator.comparingInt(PluginEntity::getPriority).reversed());
        }
    }


    /**
     * 获取服务端口(默认:8080)
     */
    public int serverPort() {
        return getInt("server.port", 8080);
    }

    /**
     * 扩展文件夹
     */
    public String extend() {
        return extend;
    }

    /**
     * 扩展文件夹过滤（.mysql.;.roperties;）
     */
    public String extendFilter() {
        return extendFilter;
    }

    /**
     * 应用名
     */
    public String appName() {
        return appName;
    }

    /**
     * 应用组
     */
    public String appGroup() {
        return appGroup;
    }

    public String appTitle() {
        return appTitle;
    }

    /**
     * 框架版本号
     */
    public String version() {
        return "1.4.14";
    }

    /**
     * 是否为 debug mode
     */
    public boolean isDebugMode() {
        return isDebugMode;
    }

    /**
     * 是否为 setup model
     * */
    public boolean isSetupMode(){ return isSetupMode; }

    /**
     * 是否为 files model (文件运行模式)
     */
    public boolean isFilesMode() {
        return isFilesMode;
    }
    public void isFilesMode(boolean isFilesMode) {
        this.isFilesMode = isFilesMode;
    }

    /**
     * 是否为 drift model (IP漂移模式，如k8s环境)
     */
    public boolean isDriftMode() {
        return isDriftMode;
    }
    public void isDriftMode(boolean isDriftMode){
        this.isDriftMode = isDriftMode;
    }

    /**
     * 是否为 while model（即白名单模式）
     */
    public boolean isWhiteMode() {
        return isWhiteMode;
    }
    public void isWhiteMode(boolean isWhiteMode){
        this.isWhiteMode = isWhiteMode;
    }
}
