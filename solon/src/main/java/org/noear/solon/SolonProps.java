/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon;

import org.noear.solon.annotation.Import;
import org.noear.solon.core.*;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.JavaUtil;
import org.noear.solon.core.util.PluginUtil;
import org.noear.solon.core.util.ResourceUtil;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

/**
 * 应用配置加载器
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
    protected final List<String> warns = new ArrayList<>();

    private final SolonApp app;
    private final NvMap args;
    private final boolean testing;
    private final List<PluginEntity> plugs = new ArrayList<>();
    private final List<String> plugsScanExcluded = new ArrayList<>();

    private boolean isDebugMode;//是否为调试模式
    private boolean isDriftMode;//是否为漂移模式（如k8s环境下,ip会不断变化）
    private boolean isFilesMode;//是否为文件模式
    private boolean isWhiteMode;//是否为名单模式（白名单模式）
    private boolean isSetupMode;//是否为安装模式
    private boolean isAloneMode;//是否为独立模式（即独立运行模式）@deprecated

    private boolean enabledVirtualThreads = false; //solon.threads.virtual.enable: true

    private String env;

    private Locale locale;

    private String extend;
    private String extendFilter;

    public SolonProps(SolonApp app, NvMap args) throws Exception {
        super(System.getProperties());

        //1.关联应用
        this.app = app;
        //1.1.接收启动参数
        this.args = args;
        //1.2.测试隔离
        this.testing = args.containsKey("testing");


        //::: 开始加载

        //2.同步启动参数到系统属性
        this.syncArgsToSys();

        //3.获取原始系统属性原始副本
        Properties sysPropOrg = new Properties();
        //3.1.把带'.'的环境变量同步到系统属性（支持弹性容器设置的环境变量）
        System.getenv().forEach((k, v) -> {
            if(k.indexOf('.') > 0){
                System.setProperty(k,v);
            }
        });
        //3.2.为系统属性建立一个原始副本
        System.getProperties().forEach((k, v) -> sysPropOrg.put(k, v));

        //4.加载文件配置
        String config = args.get("cfg"); //？限定属性文件，且不再加应用属性文件（一般用于内嵌场景）
        if(Utils.isEmpty(config)) {
            loadInit(ResourceUtil.getResource("app.properties"), sysPropOrg);
            loadInit(ResourceUtil.getResource("app.yml"), sysPropOrg);

            //4.1.加载环境变量（支持弹性容器设置的环境变量）
            //loadEnv(k -> k.indexOf('.') > 0);

            //4.2.加载环境配置(例：env=pro 或 env=debug)
            env = getArg("env");

            if (Utils.isNotEmpty(env)) {
                loadInit(ResourceUtil.getResource("app-" + env + ".properties"), sysPropOrg);
                loadInit(ResourceUtil.getResource("app-" + env + ".yml"), sysPropOrg);
            }
        } else{
            loadInit(ResourceUtil.getResource(config), sysPropOrg);

            //4.1.加载环境变量（支持弹性容器设置的环境变量）
            //loadEnv(k -> k.indexOf('.') > 0);
        }



        //4.3.加载注解配置（优于固定配置）/v1.12
        //导入自己的，同时导入注解的 Import 注解
        importPropsTry(app.source());


        //4.4.加载配置 solon.config.load //支持多文件（只支持内部，支持{env}）
        Map<String, String> loadKeyMap = new TreeMap<>();
        doFind("solon.config.load", (key, val) -> {
            if (key.equals("") || key.startsWith("[")) {
                loadKeyMap.put(key, val);
            }
        });

        for (String loadKey : loadKeyMap.values()) {
            if (loadKey.contains("*")) {
                if (ResourceUtil.hasClasspath(loadKey)) {
                    // classpath:
                    for (String loadKey0 : ResourceUtil.scanResources(loadKey)) {
                        addConfig(ResourceUtil.TAG_classpath + loadKey0, sysPropOrg);
                    }
                } else if (ResourceUtil.hasFile(loadKey)) {
                    // file:
                    for (String loadKey0 : ResourceUtil.scanResources(loadKey)) {
                        addConfig(ResourceUtil.TAG_file + loadKey0, sysPropOrg);
                    }
                } else {
                    for (String loadKey0 : ResourceUtil.scanResources(loadKey)) {
                        addConfig(loadKey0, sysPropOrg);
                    }
                }
            } else {
                addConfig(loadKey, sysPropOrg);
            }
        }

        //4.6.加载扩展配置 solon.config.add //支持多文件（支持内部或外部，支持{env}）
        addConfig(getArg("config.add"),  sysPropOrg);//替代旧的 solon.config, 与 config.load 配对


        //5.初始化模式状态

        //是否为文件模式
        isFilesMode = (app.sourceLocation().getPath().endsWith(".jar") == false
                && app.sourceLocation().getPath().contains(".jar!/") == false
                && app.sourceLocation().getPath().endsWith(".zip") == false
                && app.sourceLocation().getPath().contains(".zip!/") == false);

        if (NativeDetector.inNativeImage()) {
            //如果是原生运行
            isFilesMode = false;
        }

        //是否为调试模式
        isDebugMode = "1".equals(getArg("debug")); //调试模式
        //是否为安装模式
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

        //8.
        if (JavaUtil.JAVA_MAJOR_VERSION >= 21) {
            enabledVirtualThreads = getBool("solon.threads.virtual.enabled", false);
        }

        //9. 插件排除
        plugsScanExcluded.addAll(getList("solon.plugin.exclude"));
    }

    private void importPropsTry(Class<?> source) {
        if (source == null) {
            return;
        }

        for (Annotation a1 : source.getAnnotations()) {
            if (a1 instanceof Import) {
                loadAdd((Import) a1);
            } else {
                loadAdd(a1.annotationType().getAnnotation(Import.class));
            }
        }
    }

    private void addConfig(String paths, Properties sysPropOrg) {
        if (Utils.isNotEmpty(paths)) {
            for (String p1 : paths.split(",")) {
                URL propUrl = ResourceUtil.findResourceOrFile(null, p1);

                if (propUrl == null) {
                    //打印提醒
                    warns.add("Props: No config file: " + p1);
                } else {
                    loadInit(propUrl, sysPropOrg);
                }
            }
        }
    }

    private void syncArgsToSys() {
        //1.同步所有属性
        for(Map.Entry<String,String> kv: this.args.entrySet()) {
            if (kv.getKey().contains(".")) {
                System.setProperty(kv.getKey(), kv.getValue());
            }
        }

        //2.同步特定参数
        syncArgToSys("env");

        syncArgToSys("app.name");  //应用名
        syncArgToSys("app.group"); //应用组
        syncArgToSys("app.namespace"); //应用组
        syncArgToSys("app.title"); //应用标题

        syncArgToSys("stop.safe"); //def: 0
        syncArgToSys("stop.delay"); //def: 10s
    }

    /**
     * 同步特定启动参数到系统属性
     *
     * @param name 参数名
     * */
    private void syncArgToSys(String name) {
        String val = args.get(name);
        if (val != null) {
            //如果为空，尝试从属性配置取
            System.setProperty("solon." + name, val);
        }
    }

    /**
     * 获取启动参数
     *
     * @param name 参数名
     */
    private String getArg(String name) {
        //尝试去启动参数取
        String val = args.get(name);
        if (val == null) {
            //如果为空，尝试从属性配置取
            val = get("solon." + name);
        }

        return val;
    }

    /**
     * 加载环境变量
     *
     * @param keyStarts key 的开始字符
     */
    public SolonProps loadEnv(String keyStarts) {
        return loadEnv(k -> k.startsWith(keyStarts));
    }

    /**
     * 加载环境变量
     *
     * @param condition 条件
     */
    public SolonProps loadEnv(Predicate<String> condition) {
        System.getenv().forEach((k, v) -> {
            if (condition.test(k)) {
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
        loadAddDo(props, app.isMain() || testing == false, false);
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
            //同步系统属性
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
            PluginUtil.scanPlugins(classLoader, plugsScanExcluded, plugs::add);
        }

        //扫描主配置
        PluginUtil.findPlugins(AppClassLoader.global(), this, plugsScanExcluded, plugs::add);

        //插件排序
        plugsSort();
    }

    /**
     * 插件扫描排除
     *
     * @since 3.0
     * */
    protected void plugsScanExclude(String className) {
        plugsScanExcluded.add(className);
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
            Collections.sort(plugs);
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
     *
     * @param useRaw 使用原始数据（否则没有时，使用 `serverPort` 值）
     */
    public int serverWrapPort(boolean useRaw) {
        if (serverWrapPort == null) {
            serverWrapPort = getInt("server.wrapPort", 0);
        }

        if (useRaw || serverWrapPort > 0) {
            return serverWrapPort;
        } else {
            return serverPort();
        }
    }

    private String serverWrapHost;

    /**
     * 获取应用包装主机
     *
     * @param useRaw 使用原始数据（否则没有时，使用 `serverHost` 值）
     */
    public String serverWrapHost(boolean useRaw) {
        if (serverWrapHost == null) {
            serverWrapHost = get("server.wrapHost", "");
        }

        if (useRaw || Utils.isNotEmpty(serverWrapHost)) {
            return serverWrapHost;
        } else {
            return serverHost();
        }
    }


    private String serverContextPath;
    private boolean serverContextPathForced;

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

    public boolean serverContextPathForced() {
        //初始化
        serverContextPath();
        return serverContextPathForced;
    }

    /**
     * 设置服务主上下文路径
     *
     * @param path 上下文路径
     */
    public void serverContextPath(String path) {
        if (path == null) {
            serverContextPath = "";
        } else {
            serverContextPath = path;
        }

        if (serverContextPath.length() > 0) {

            if (serverContextPath.startsWith("!")) {
                serverContextPathForced = true;
                serverContextPath = serverContextPath.substring(1);
            } else {
                serverContextPathForced = false;
            }

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
     */
    public boolean testing() {
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
        return get("solon.app.name");
    }

    /**
     * 应用组
     */
    public String appGroup() {
        return get("solon.app.group");
    }

    /**
     * 命名空间
     */
    public String appNamespace() {
        return get("solon.app.namespace");
    }

    /**
     * 应用标题
     */
    public String appTitle() {
        return get("solon.app.title");
    }

    /**
     * 应用许可证
     * */
    public String appLicence(){
        return get("solon.app.licence", "");
    }

    /**
     * 应用健康状况
     * */
    public boolean appEnabled() {
        return getBool("solon.app.enabled", true);
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
     *
     * @deprecated 3.0
     */
    @Deprecated
    public boolean isAloneMode() {
        return isAloneMode;
    }

    /**
     * 设置独立模式
     *
     * @deprecated
     */
    @Deprecated
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


    private Boolean stopSafe;
    /**
     * 停止安全的进行
     */
    public boolean stopSafe() {
        if(stopSafe == null){
            stopSafe = "1".equals(get("solon.stop.safe"));
        }

        return stopSafe;
    }

    public void stopSafe(boolean value) {
        stopSafe = value;
    }

    private Integer stopDelay;
    /**
     * 停止延时
     */
    public int stopDelay() {
        if(stopDelay == null){
            stopDelay = Integer.parseInt(get("solon.stop.delay", "10s").replace("s", ""));
        }

        return stopDelay;
    }

    public boolean isEnabledVirtualThreads() {
        return enabledVirtualThreads;
    }
}