package org.noear.solon;

import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.PluginEntity;
import org.noear.solon.core.Props;
import org.noear.solon.core.util.ScanUtil;

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
 * // 配置的优先级：命令参数-> 环境配置-> 系统配置-> 应用配置 （越动态的越优化）
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
    private final List<PluginEntity> plugs = new ArrayList<>();
    private boolean isDebugMode;//是否为调试模式
    private boolean isDriftMode;//是否为漂移模式（如k8s环境下,ip会不断变化）
    private boolean isFilesMode;//是否为文件模式
    private boolean isWhiteMode;//是否为的名单模式
    private boolean isSetupMode;//是否为安装蕈式
    private boolean isAloneMode;//是否为独立蕈式（即独立运行模式）

    private String env;

    private Locale locale;

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
        //1.1.应用源
        this.source = source;
        //1.2.应用源位置
        this.sourceLocation = source.getProtectionDomain().getCodeSource().getLocation();


        //2.同步启动参数到系统属性
        this.args.forEach((k, v) -> {
            if (k.contains(".")) {
                System.setProperty(k,v);
            }
        });

        //3.获取原始系统属性原始副本
        Properties sysPropOrg = new Properties();
        System.getProperties().forEach((k, v) -> sysPropOrg.put(k, v));


        //4.加载文件配置
        //@Deprecated
        loadInit(Utils.getResource("application.properties"), sysPropOrg);
        //@Deprecated
        loadInit(Utils.getResource("application.yml"), sysPropOrg);
        loadInit(Utils.getResource("app.properties"), sysPropOrg);
        loadInit(Utils.getResource("app.yml"), sysPropOrg);

        //4.1.加载环境变量（支持弹性容器设置的环境变量）
        loadEnv("solon.");

        //4.2.加载环境配置(例：env=pro 或 env=debug)
        env = getArg("env");

        if (Utils.isEmpty(env)) {
            //@Deprecated
            env = getArg("profiles.active");
        }

        if (Utils.isNotEmpty(env)) {
            //@Deprecated
            loadInit(Utils.getResource("application-" + env + ".properties"), sysPropOrg);
            //@Deprecated
            loadInit(Utils.getResource("application-" + env + ".yml"), sysPropOrg);
            loadInit(Utils.getResource("app-" + env + ".properties"), sysPropOrg);
            loadInit(Utils.getResource("app-" + env + ".yml"), sysPropOrg);
        }


        //5.初始化模式状态

        //是否为文件模式
        isFilesMode = (sourceLocation.getPath().endsWith(".jar") == false
                && sourceLocation.getPath().contains(".jar!/") == false
                && sourceLocation.getPath().endsWith(".zip") == false
                && sourceLocation.getPath().contains(".zip!/") == false);

        //是否为调试模式
        isDebugMode = "1".equals(getArg("debug")); //调试模式
        //是否为调试模式
        isSetupMode = "1".equals(getArg("setup")); //安装模式
        //是否为白名单模式
        isWhiteMode = "1".equals(getArg("white")); //安全模式（即白名单模式）//todo:默认不再为1, update by 2021.11.19
        //是否为漂移模式
        isDriftMode = "1".equals(getArg("drift")); //漂移模式（即ip会变,如k8s部署）
        //是否为独立模式
        isAloneMode = "1".equals(getArg("alone")); //独立模式

        //标识debug模式
        if (isDebugMode()) {
            System.setProperty("debug", "1");
        }


        //6.确定扩展文件夹
        extend = getArg("extend");
        extendFilter = getArg("extend.filter");//5.1.扩展文件夹过滤器


        //7.确定地区配置
        String localeStr = getArg("locale");
        if (Utils.isNotEmpty(localeStr)) {
            locale = Utils.toLocale(localeStr);
            Locale.setDefault(locale);
        } else {
            locale = Locale.getDefault();
        }

        //8.应用基础信息
        appName = getArg("app.name");  //6.应用名
        appGroup = getArg("app.group"); //6.1.应用组
        appTitle = getArg("app.title"); //6.1.应用标题

        return this;
    }


    /**
     * 获取启动参数
     *
     * @param name 参数名
     */
    private String getArg(String name) {
        return getArg(name, null);
    }

    /**
     * 获取启动参数
     *
     * @param name 参数名
     * @param def  默认值
     */
    private String getArg(String name, String def) {
        //尝试去启动参数取
        String tmp = args.get(name);
        if (Utils.isEmpty(tmp)) {
            //如果为空，尝试从属性配置取
            tmp = get("solon." + name);
        }

        if (Utils.isEmpty(tmp)) {
            return def;
        } else {
            return tmp;
        }
    }

    /**
     * 加载环境变量
     *
     * @param keyStarts key 的开始字符
     */
    public SolonProps loadEnv(String keyStarts) {
        System.getenv().forEach((k, v) -> {
            if (k.startsWith(keyStarts)) {
                setProperty(k, v); //可以替换系统属性 update by: 2021-11-05,noear
                System.setProperty(k, v);
            }
        });

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
     *
     * @param url 配置地址
     */
    public SolonProps loadAdd(String url) {
        return loadAdd(Utils.getResource(url));
    }

    /**
     * 加载配置（用于扩展加载）
     *
     * @param props 配置地址
     */
    public SolonProps loadAdd(Properties props) {
        if (props != null) {
            for (Map.Entry<Object, Object> kv : props.entrySet()) {
                Object k1 = kv.getKey();
                Object v1 = kv.getValue();

                if (k1 instanceof String) {
                    String key = (String) k1;

                    if (Utils.isEmpty(key)) {
                        continue;
                    }

                    if (v1 instanceof String) {
                        // db1.url=xxx
                        // db1.jdbcUrl=${db1.url}
                        // db1.jdbcUrl=jdbc:mysql:${db1.server}
                        // db1.jdbcUrl=jdbc:mysql:${db1.server}/${db1.db}
                        String v1Str = (String) v1;
                        int symStart = 0;

                        while (true) {
                            symStart = v1Str.indexOf("${", symStart);
                            if (symStart >= 0) {
                                int symEnd = v1Str.indexOf("}", symStart + 1);
                                if (symEnd > symStart) {
                                    String tmpK = v1Str.substring(symStart + 2, symEnd);

                                    String tmpV2 = props.getProperty(tmpK);
                                    if (tmpV2 == null) {
                                        tmpV2 = getProperty(tmpK);
                                    }

                                    if (tmpV2 == null) {
                                        symStart = symEnd;
                                    } else {
                                        if (symStart > 0) {
                                            //确定左侧部分
                                            tmpV2 = v1Str.substring(0, symStart) + tmpV2;
                                        }
                                        symStart = tmpV2.length();
                                        v1Str = tmpV2 + v1Str.substring(symEnd + 1);
                                    }
                                } else {
                                    //找不到 "}"，则终止
                                    break;
                                }
                            } else {
                                //找不到 "${"，则终止
                                break;
                            }
                        }

                        v1 = v1Str;
                    }

                    if (v1 != null) {
                        System.getProperties().put(k1, v1);
                        put(k1, v1);
                    }
                }
            }
        }

        return this;
    }

    /**
     * 加载初始化配置
     * <p>
     * 1.优先使用 system properties；可以在启动时修改配置
     * 2.之后同时更新 system properties 和 solon cfg
     */
    protected void loadInit(URL url, Properties sysPropOrg) {
        if (url != null) {
            Properties props = Utils.loadProperties(url);

            if (props == null) {
                //说明 url 解析失败了!
                return;
            }

            for (Map.Entry kv : sysPropOrg.entrySet()) {
                if (kv.getKey() instanceof String) {
                    String key = (String) kv.getKey();

                    if (Utils.isEmpty(key)) {
                        continue;
                    }

                    if (props.containsKey(key)) {
                        props.put(key, kv.getValue());
                    }
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
            ScanUtil.scan(classLoader, "META-INF/solon", n -> n.endsWith(".properties") || n.endsWith(".yml"))
                    .stream()
                    .map(k -> Utils.getResource(classLoader, k))
                    .forEach(url -> plugsScanMapDo(classLoader, url));
        }

        //扫描主配置
        plugsScanLoadDo(JarClassLoader.global(), this);

        //插件排序
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
                    ent.setPriority(priority);
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

    /**
     * 设置应用属性
     */
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
     * 应用源
     */
    public Class<?> source() {
        return source;
    }

    /**
     * 应用源位置
     */
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
     * 对插件列表排序
     */
    public void plugsSort() {
        if (plugs.size() > 0) {
            //进行优先级顺排（数值要倒排）
            //
            plugs.sort(Comparator.comparingInt(PluginEntity::getPriority).reversed());
        }
    }


    private int serverPort;

    /**
     * 获取应用主端口(默认:8080)
     */
    public int serverPort() {
        if (serverPort == 0) {
            serverPort = getInt("server.port", 8080);
        }

        return serverPort;
    }


    /**
     * 环境
     */
    public String env() {
        return env;
    }

    /**
     * 地区
     */
    public Locale locale() {
        return locale;
    }

    /**
     * 扩展文件夹
     */
    public String extend() {
        return extend;
    }

    /**
     * 扩展文件夹过滤（.mysql.,.yml）
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

    /**
     * 应用标题
     */
    public String appTitle() {
        return appTitle;
    }


    /**
     * 框架版本号
     */
    public String version() {
        return "1.6.27-m2";
    }

    /**
     * 是否为调试模式
     */
    public boolean isDebugMode() {
        return isDebugMode;
    }

    /**
     * 是否为安装模式
     */
    public boolean isSetupMode() {
        return isSetupMode;
    }

    /**
     * 是否为文件运行模式（否则为包执行模式）
     */
    public boolean isFilesMode() {
        return isFilesMode;
    }

    /**
     * 设置文件运行模式
     */
    public void isFilesMode(boolean isFilesMode) {
        this.isFilesMode = isFilesMode;
    }

    /**
     * 是否为漂移模式
     */
    public boolean isDriftMode() {
        return isDriftMode;
    }

    /**
     * 设置漂移模式
     */
    public void isDriftMode(boolean isDriftMode) {
        this.isDriftMode = isDriftMode;
    }

    /**
     * 是否为独立模式
     */
    public boolean isAloneMode() {
        return isAloneMode;
    }

    /**
     * 设置独立模式
     */
    public void isAloneMode(boolean isAloneMode) {
        this.isAloneMode = isAloneMode;
    }

    /**
     * 是否为白名单模式
     */
    public boolean isWhiteMode() {
        return isWhiteMode;
    }

    /**
     * 设置白名单模式
     */
    public void isWhiteMode(boolean isWhiteMode) {
        this.isWhiteMode = isWhiteMode;
    }
}
