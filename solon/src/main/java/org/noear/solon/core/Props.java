package org.noear.solon.core;

import org.noear.solon.SolonProps;
import org.noear.solon.Utils;
import org.noear.solon.annotation.PropertySource;
import org.noear.solon.core.util.ResourceUtil;

import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 通用属性集合（为 SolonProps 的基类）
 *
 * 在 Properties 基础上，添加了些方法
 *
 * @see SolonProps
 * @author noear
 * @since 1.0
 * */
public class Props extends Properties {
    private ClassLoader classLoader;

    public Props() {
        //不产生 defaults
        super();
    }

    public Props(ClassLoader classLoader) {
        super();
        this.classLoader = classLoader;
    }

    public Props(Properties defaults) {
        super(defaults);
    }

    public Props(Map<String, String> data) {
        super();
        super.putAll(data);
    }

    @Override
    public synchronized int size() {
        if (defaults == null) {
            return super.size();
        } else {
            return super.size() + defaults.size();
        }
    }

    /**
     * 获取属性
     */
    public String get(String key) {
        return getProperty(key);
    }

    public String getOr(String key, String key2) {
        String tmp = getProperty(key);
        if (Utils.isEmpty(tmp)) {
            return getProperty(key2);
        } else {
            return tmp;
        }
    }

    /**
     * @param expr 兼容 ${key} or key or ${key:def} or key:def
     */
    public String getByExpr(String expr) {
        return getByExpr(expr, null);
    }

    /**
     * @param expr 兼容 ${key} or key or ${key:def} or key:def
     */
    protected String getByExpr(String expr, Properties props) {
        String name = expr;

        //如果有表达式，去掉符号
        if (name.startsWith("${") && name.endsWith("}")) {
            name = expr.substring(2, name.length() - 1);
        }

        //如果有默认值，则获取
        String def = null;
        int defIdx = name.indexOf(":");
        if (defIdx > 0) {
            if (name.length() > defIdx + 1) {
                def = name.substring(defIdx + 1).trim();
            } else {
                def = "";
            }
            name = name.substring(0, defIdx).trim();
        }


        String val = null;

        if (props != null) {
            //从"目标属性"获取
            val = props.getProperty(name);
        }

        if (val == null) {
            //从"本属性"获取
            val = get(name);

            if (val == null && Character.isUpperCase(name.charAt(0))) {
                //从"环镜变量"获取
                val = System.getenv(name);
            }
        }

        if (val == null) {
            return def;
        } else {
            return val;
        }
    }

    /**
     * @param tml 模板： ${key} 或 aaa${key}bbb
     */
    public String getByParse(String tml) {
        return getByParse(tml, null);
    }

    /**
     * @param tml 模板： ${key} 或 aaa${key}bbb
     */
    protected String getByParse(String tml, Properties props) {
        if (Utils.isEmpty(tml)) {
            return tml;
        }

        int start = 0, end = 0;
        while (true) {
            start = tml.indexOf("${", start);

            if (start < 0) {
                return tml;
            } else {
                end = tml.indexOf("}", start);

                if (end < 0) {
                    throw new IllegalStateException("Invalid template expression: " + tml);
                }

                String name = tml.substring(start + 2, end);
                String value = getByExpr(name, props);//支持默认值表达式
                if (value == null) {
                    value = "";
                }

                tml = tml.substring(0, start) + value + tml.substring(end + 1);

                //起始位增量
                start = start + value.length();
            }
        }
    }

    /**
     * 获取某项配置（如果没有，输出默认值）
     *
     * @param def 默认值
     */
    public String get(String key, String def) {
        return getProperty(key, def);
    }

    /**
     * 获取某项配置，并转为布尔型（如果没有，输出默认值）
     *
     * @param def 默认值
     */
    public boolean getBool(String key, boolean def) {
        return getOrDef(key, def, Boolean::parseBoolean);
    }

    /**
     * 获取某项配置，并转为整型（如果没有，输出默认值）
     *
     * @param def 默认值
     */
    public int getInt(String key, int def) {
        return getOrDef(key, def, Integer::parseInt);
    }

    /**
     * 获取某项配置，并转为长整型（如果没有，输出默认值）
     *
     * @param def 默认值
     */
    public long getLong(String key, long def) {
        return getOrDef(key, def, Long::parseLong);
    }

    /**
     * 获取某项配置，并转为又精度型（如果没有，输出默认值）
     *
     * @param def 默认值
     */
    public Double getDouble(String key, double def) {
        return getOrDef(key, def, Double::parseDouble);
    }

    private <T> T getOrDef(String key, T def, Function<String, T> convert) {
        String temp = get(key);
        if (Utils.isEmpty(temp)) {
            return def;
        } else {
            return convert.apply(temp);
        }
    }


    /**
     * 查找 keyStarts 开头的所有配置；并生成一个新的 Bean
     *
     * @param keyStarts key 的开始字符
     */
    public <T> T getBean(String keyStarts, Class<T> clz) {
        Properties props = getProp(keyStarts);
        return PropsConverter.global().convert(props, clz);
    }

    public <T> T getBean(Class<T> clz) {
        return PropsConverter.global().convert(this, clz);
    }

    public <T> T bindTo(T obj) {
        PropsConverter.global().convert(this, obj, null, null);
        return obj;
    }

    /**
     * 查找 keyStarts 开头的所有配置；并生成一个新的 配置集
     *
     * @param keyStarts key 的开始字符
     */
    public Props getProp(String keyStarts) {
        if (Utils.isEmpty(keyStarts)) {
            return this;
        } else {
            Props prop = new Props();

            doFind(keyStarts, (key, val) -> { //相对旧版，减少一次 forEach
                if (key.startsWith(".")) {
                    key = key.substring(1); //去掉 .
                    prop.put(key, val);

                    if (key.contains("-")) {//为带 - 的 key ，增加副本
                        String camelKey = buildCamelKey(key);
                        prop.put(camelKey, val);
                    }
                } else if (key.startsWith("[")) {
                    prop.put(key, val);
                }
            });

            return prop;
        }
    }

    /**
     * 查找 keyStarts 开头的所有配置；并生成一个新的分组的配置集
     *
     * @param keyStarts key 的开始字符
     */
    public Map<String, Props> getGroupedProp(String keyStarts) {
        Props rootProps = getProp(keyStarts);

        Set<String> groups = new HashSet<>();
        for (Object key : rootProps.keySet()) {
            if (key instanceof String) {
                groups.add(((String) key).split("\\.")[0]);
            }
        }

        Map<String, Props> groupProps = new HashMap<>();

        for (String group : groups) {
            Props tmp = rootProps.getProp(group);
            groupProps.put(group, tmp);
        }

        return groupProps;
    }

    /**
     * @param expr 兼容 ${key} or key
     */
    public Props getPropByExpr(String expr) {
        String name = expr;
        if (name.startsWith("${") && name.endsWith("}")) {
            name = expr.substring(2, name.length() - 1);
        }

        return getProp(name);
    }

    /**
     * 兼容旧的
     *
     * @deprecated 2.2
     * */
    @Deprecated
    public NvMap getXmap(String keyStarts) {
        NvMap map = new NvMap();
        doFind(keyStarts + ".", map::put);
        return map;
    }

    /**
     * 查找 keyStarts 开头的所有配置；并生成一个新的 Map
     *
     * @param keyStarts key 的开始字符
     */
    public Map<String,String> getMap(String keyStarts) {
        Map<String, String> map = new LinkedHashMap<>();
        doFind(keyStarts, map::put);
        return map;
    }

    /**
     * 查找 keyStarts 开头的所有配置；并生成一个新的 List
     *
     * @param keyStarts key 的开始字符
     */
    public List<String> getList(String keyStarts) {
        Map<String, String> sortMap = new TreeMap<>();
        doFind(keyStarts + "[", (k, v) -> {
            sortMap.put(k, v);
        });
        return new ArrayList<>(sortMap.values());
    }

    private void doFind(String keyStarts, BiConsumer<String, String> setFun) {
        String key2 = keyStarts;
        int idx2 = key2.length();

        forEach((k, v) -> {
            if (k instanceof String && v instanceof String) {
                String keyStr = (String) k;

                if (keyStr.startsWith(key2)) {
                    String key = keyStr.substring(idx2);

                    setFun.accept(key, (String) v);

                    if (key.contains("-")) {
                        String camelKey = buildCamelKey(key);
                        setFun.accept(camelKey, (String) v);
                    }
                }
            }
        });
    }

    /**
     * 重写 forEach，增加 defaults 的遍历
     */
    @Override
    public synchronized void forEach(BiConsumer<? super Object, ? super Object> action) {
        super.forEach(action);

        if (defaults != null) {
            defaults.forEach((k, v) -> {
                if (super.containsKey(k) == false) {
                    action.accept(k, v);
                }
            });
        }
    }

    ////

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

    public synchronized void putIfNotNull(Object key, Object value) {
        if (key != null && value != null) {
            this.put(key, value);
        }
    }

    ////

    /**
     * 加载配置（用于扩展加载）
     *
     * @param name 资源名
     */
    public void loadAdd(String name) {
        loadAdd(ResourceUtil.getResource(classLoader, name));
    }

    public void loadAdd(PropertySource propertySource) {
        if (propertySource == null) {
            return;
        }

        for (String uri : propertySource.value()) {
            uri = getByParse(uri);
            loadAdd(ResourceUtil.findResource(classLoader, uri));
        }
    }

    /**
     * 加载配置（用于扩展加载）
     *
     * @param url 配置地址
     */
    public void loadAdd(URL url) {
        if (url != null) {
            Properties props = Utils.loadProperties(url);
            loadAdd(props);
        }
    }

    /**
     * 加载配置（用于扩展加载）
     */
    public void loadAdd(Properties props) {
        loadAddDo(props, false, false);
    }


    /**
     * 加载配置（用于扩展加载）
     *
     * @param name 资源名
     */
    public void loadAddIfAbsent(String name) {
        loadAddIfAbsent(ResourceUtil.getResource(classLoader, name));
    }

    /**
     * 加载配置（用于扩展加载）
     *
     * @param url 配置地址
     */
    public void loadAddIfAbsent(URL url) {
        if (url != null) {
            Properties props = Utils.loadProperties(url);
            loadAddIfAbsent(props);
        }
    }

    /**
     * 加载配置（用于扩展加载）
     */
    public void loadAddIfAbsent(Properties props) {
        loadAddDo(props, false, true);
    }

    /**
     * 加载配置（用于扩展加载）
     *
     * @param props 配置地址
     */
    protected void loadAddDo(Properties props, boolean toSystem, boolean addIfAbsent) {
        if (props != null) {
            for (Map.Entry<Object, Object> kv : props.entrySet()) {
                Object k1 = kv.getKey();
                Object v1 = kv.getValue();

                if (addIfAbsent) {
                    //如果已存在，则不盖掉
                    if (containsKey(k1)) {
                        continue;
                    }
                }

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
                        v1 = getByParse((String) v1, props);
                    }

                    if (v1 != null) {
                        if (toSystem) {
                            System.getProperties().put(k1, v1);
                        }

                        put(k1, v1);


                        if (key.contains("-")) {
                            String camelKey = buildCamelKey(key);
                            put(camelKey, v1);
                        }
                    }
                }
            }
        }
    }

    /**
     * 将 - 转为小驼峰key
     */
    private String buildCamelKey(String key) {
        String[] ss = key.split("-");
        StringBuilder sb = new StringBuilder(key.length());
        sb.append(ss[0]);
        for (int i = 1; i < ss.length; i++) {
            if (ss[i].length() > 1) {
                sb.append(ss[i].substring(0, 1).toUpperCase()).append(ss[i].substring(1));
            } else {
                sb.append(ss[i].toUpperCase());
            }
        }

        return sb.toString();
    }
}
