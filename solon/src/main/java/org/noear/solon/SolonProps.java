package org.noear.solon;

import org.noear.solon.annotation.PropertySource;
import org.noear.solon.core.*;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.PluginUtil;
import org.noear.solon.core.util.ResourceUtil;

import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

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
    private boolean testing;
    private final List<PluginEntity> plugs = new ArrayList<>();

    private boolean isDebugMode;//是否为调试模式
    private boolean isDriftMode;//是否为漂移模式（如k8s环境下,ip会不断变化）
    private boolean isFilesMode;//是否为文件模式
    private boolean isWhiteMode;//是否为名单模式（白名单模式）
    private boolean isSetupMode;//是否为安装蕈式
    private boolean isAloneMode;//是否为独立蕈式（即独立运行模式）

    private int stopDelay=10; //停止延迟（秒）
    private boolean stopSafe;//停止安全的进行

    private String env;

    private Locale locale;

    private String extend;
    private String extendFilter;

    private String appName;
    private String appGroup;
    private String appNamespace;
    private String appTitle;

    public SolonProps() {
        super(System.getProperties());
    }

    /**
     * 加载配置（用于第一次加载）
     *
     * @param args 启用参数
     */
    public SolonProps load(Class<?> source, NvMap args) throws Exception {
        //1.接收启动参数
        this.args = args;
        //1.1.应用源
        this.source = source;
        //1.2.应用源位置
        this.sourceLocation = source.getProtectionDomain().getCodeSource().getLocation();
        //1.3.测试隔离
        this.testing = args.containsKey("testing");

        //2.同步启动参数到系统属性
        this.args.forEach((k, v) -> {
            if (k.contains(".")) {
                System.setProperty(k, v);
            }
        });

        //3.获取原始系统属性原始副本
        Properties sysPropOrg = new Properties();
        System.getProperties().forEach((k, v) -> sysPropOrg.put(k, v));


        URL appUrl;

        //4.加载文件配置
        //@Deprecated 2.2
        appUrl = ResourceUtil.getResource("application.properties");
        if (appUrl != null) {
            loadInit(appUrl, sysPropOrg);
            profileWran("application.properties");
        }

        //@Deprecated 2.2
        appUrl = ResourceUtil.getResource("application.yml");
        if (appUrl != null) {
            loadInit(appUrl, sysPropOrg);
            profileWran("application.yml");
        }

        loadInit(ResourceUtil.getResource("app.properties"), sysPropOrg);
        loadInit(ResourceUtil.getResource("app.yml"), sysPropOrg);

        //4.1.加载环境变量（支持弹性容器设置的环境变量）
        loadEnv(k -> k.startsWith("solon.") || k.startsWith("server."));

        //4.2.加载环境配置(例：env=pro 或 env=debug)
        env = getArg("env");

        if (Utils.isNotEmpty(env)) {
            //@Deprecated 2.2
            appUrl = ResourceUtil.getResource("application-" + env + ".properties");
            if (appUrl != null) {
                loadInit(appUrl, sysPropOrg);
                profileWran("application-" + env + ".properties");
            }

            //@Deprecated 2.2
            appUrl = ResourceUtil.getResource("application-" + env + ".yml");
            if (appUrl != null) {
                loadInit(appUrl, sysPropOrg);
                profileWran("application-" + env + ".yml");
            }

            loadInit(ResourceUtil.getResource("app-" + env + ".properties"), sysPropOrg);
            loadInit(ResourceUtil.getResource("app-" + env + ".yml"), sysPropOrg);
        }


        //4.3.加载注解配置（优于固定配置）/v1.12
        loadAdd(source.getAnnotation(PropertySource.class));

        //4.4.加载配置 solon.config.load //支持多文件（只支持内部，支持{env}）
        getMap("solon.config.load").forEach((key, val)->{
            if(key.equals("") || key.startsWith("[")) {
                addConfig(val, true, sysPropOrg);
            }
        });


        //4.5.加载扩展配置 solon.config.add //支持多文件（支持内部或外部，支持{env}）
        addConfig(getArg("config"), false, sysPropOrg);//@Deprecated 2.2
        addConfig(getArg("config.add"), false, sysPropOrg);//替代旧的 solon.config, 与 config.load 配对


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
        isWhiteMode = "1".equals(getArg("white")); //安全模式（即白名单模式）//todo:默认不再为1, update by 2021.11.49
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
        appNamespace = getArg("app.namespace"); //6.1.应用组
        appTitle = getArg("app.title"); //6.1.应用标题

        //9.特性控制
        //solon.stop.delay = 10
        //solon.stop.safe  = 0
        String stopSafeStr = getArg("stop.safe");
        if (Utils.isEmpty(stopSafeStr)) {
            //@deprecated
            stopSafeStr = getArg("app.safeStop");
        }
        stopSafe = "1".equals(stopSafeStr); //是否安全停止
        stopDelay = Integer.parseInt(getArg("stop.delay", "10s").replace("s", ""));

        return this;
    }

    private void addConfig(String vals, boolean isName, Properties sysPropOrg) {
        if (Utils.isNotEmpty(vals)) {
            for (String val : vals.split(",")) {
                URL propUrl = (isName ? ResourceUtil.getResource(val) : ResourceUtil.findResource(val));

                if (propUrl == null) {
                    LogUtil.global().warn("Props: No config file: " + val);
                } else {
                    loadInit(propUrl, sysPropOrg);
                }
            }
        }
    }

    private void profileWran(String file) {
        String sml = file.replace("application", "app");
        LogUtil.global().warn("'" + file + "' is deprecated, please use '" + sml + "'");
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
        return loadEnv(k -> k.startsWith(keyStarts));
    }

    public SolonProps loadEnv(Predicate<String> predicate) {
        System.getenv().forEach((k, v) -> {
            if (predicate.test(k)) {
                setProperty(k, v); //可以替换系统属性 update by: 2021-11-05,noear
                System.setProperty(k, v);
            }
        });

        return this;
    }


    /**
     * 加载配置（用于扩展加载）
     *
     * @param props 配置地址
     */
    @Override
    public void loadAdd(Properties props) {
        loadAddDo(props, testing == false, false);
    }

    /**
     * 加载初始化配置
     * <p>
     * 1.优先使用 system properties；可以在启动时修改配置
     * 2.之后同时更新 system properties 和 solon cfg
     */
    protected void loadInit(URL url, Properties sysPropOrg) {
        if (url == null) {
            return;
        }

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


    /**
     * 插件扫描
     */
    protected void plugsScan(List<ClassLoader> classLoaders) {
        for (ClassLoader classLoader : classLoaders) {
            //扫描配置
            PluginUtil.scanPlugins(classLoader, null ,plugs::add);
        }

        //扫描主配置
        PluginUtil.findPlugins(JarClassLoader.global(), this, plugs::add);

        //插件排序
        plugsSort();
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

    private String serverHost;
    
    /**
     * 获取应用主机名
     */
    public String serverHost() {
        if (serverHost == null) {
            serverHost = get("server.host", "");
        }

        return serverHost;
    }



    private Integer serverWrapPort;

    /**
     * 获取应用包装主端口(默认:8080)
     */
    public int serverWrapPort(boolean raw) {
        if (serverWrapPort == null) {
            serverWrapPort = getInt("server.wrapPort", 0);
        }

        if (raw || serverWrapPort > 0) {
            return serverWrapPort;
        } else {
            return serverPort();
        }
    }

    private String serverWrapHost;
    /**
     * 获取应用包装主机
     */
    public String serverWrapHost(boolean raw) {
        if (serverWrapHost == null) {
            serverWrapHost = get("server.wrapHost", "");
        }

        if (raw || Utils.isNotEmpty(serverWrapHost)) {
            return serverWrapHost;
        } else {
            return serverHost();
        }
    }


    private String serverContextPath;
    /**
     * 获取服务主上下文路径
     */
    public String serverContextPath() {
        if (serverContextPath == null) {
            String path = get("server.contextPath", "").trim();
            serverContextPath(path);
        }

        return serverContextPath;
    }

    /**
     * 设置服务主上下文路径
     *
     * @param path 上下文路径
     * */
    public void serverContextPath(String path) {
        if (path == null) {
            serverContextPath = "";
        } else {
            serverContextPath = path;
        }

        if (serverContextPath.length() > 0) {
            //确保是 / 开头
            if (serverContextPath.startsWith("/") == false) {
                serverContextPath = "/" + serverContextPath;
            }

            //确保是 / 结尾
            if (serverContextPath.endsWith("/") == false) {
                serverContextPath = serverContextPath + "/";
            }
        }
    }


    /**
     * 环境
     */
    public String env() {
        return env;
    }

    /**
     * 是否为单测
     * */
    public boolean testing(){
        return testing;
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
     * 命名空间
     * */
    public String appNamespace() {
        return appNamespace;
    }

    /**
     * 应用标题
     */
    public String appTitle() {
        return appTitle;
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
    public void isFilesMode(boolean value) {
        this.isFilesMode = value;
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
    public void isDriftMode(boolean value) {
        this.isDriftMode = value;
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
    public void isAloneMode(boolean value) {
        this.isAloneMode = value;
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
    public void isWhiteMode(boolean value) {
        this.isWhiteMode = value;
    }


    /**
     * 停止安全的进行
     * */
    public boolean stopSafe(){
        return stopSafe;
    }

    public void stopSafe(boolean value){
         stopSafe = value;
    }

    /**
     * @deprecated 2.0
     * */
    @Deprecated
    public boolean enableSafeStop(){
        return stopSafe;
    }

    /**
     * 停止延时
     * */
    public int stopDelay() {
        return stopDelay;
    }
}