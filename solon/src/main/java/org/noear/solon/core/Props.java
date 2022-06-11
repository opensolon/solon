package org.noear.solon.core;

import org.noear.solon.SolonProps;
import org.noear.solon.Utils;
import org.noear.solon.core.wrap.ClassWrap;

import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 通用属性集合（为 SolonProperties 的基类）
 *
 * 在 Properties 基础上，添加了些方法
 *
 * @see SolonProps
 * @author noear
 * @since 1.0
 * */
public class Props extends Properties {

    public Props() {
        super();
    }

    public Props(Properties defaults) {
        super(defaults);
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
     * 获取某项配置
     */
    public String get(String key) {
        return getProperty(key);
    }

    /**
     * @param expr 兼容 ${key} or key
     */
    public String getByExpr(String expr) {
        String name = expr;
        if (name.startsWith("${") && name.endsWith("}")) {
            name = expr.substring(2, name.length() - 1);
        }

        return get(name);
    }

    /**
     * @param expr 兼容 ${key} or key
     */
    public String getByParse(String expr) {
        if (Utils.isEmpty(expr)) {
            return expr;
        }

        int start = expr.indexOf("${");
        if (start < 0) {
            return expr;
        } else {
            int end = expr.indexOf("}");
            String name = expr.substring(start + 2, end);
            String value = get(name);
            return expr.substring(0, start) + value + expr.substring(end + 1);
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
        return PropsConverter.global().convert(props, null, clz, null);
    }

    /**
     * 查找 keyStarts 开头的所有配置；并生成一个新的 配置集
     *
     * @param keyStarts key 的开始字符
     */
    public Props getProp(String keyStarts) {
        Props prop = new Props();
        doFind(keyStarts + ".", prop::put);
        if (prop.size() == 0) {
            doFind(keyStarts + "[", (k, v) -> {
                prop.put("[" + k, v);
            });
        }
        return prop;
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
     * 查找 keyStarts 开头的所有配置；并生成一个新的 Map
     *
     * @param keyStarts key 的开始字符
     */
    public NvMap getXmap(String keyStarts) {
        NvMap map = new NvMap();
        doFind(keyStarts + ".", map::put);
        return map;
    }

//    public List<String> getList(String keyStarts) {
//        List<String> ary = new ArrayList<>();
//        doFind(keyStarts + "[", (k, v) -> {
//            ary.add(v);
//        });
//        return ary;
//    }

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
                        setFun.accept(sb.toString(), (String) v);
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
        if (defaults == null) {
            super.forEach(action);
        } else {
            defaults.forEach(action);
            super.forEach((k, v) -> {
                if (defaults.containsKey(k) == false) {
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

    ////

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

    public void loadAdd(Properties props) {
        loadAddDo(props, false);
    }

    /**
     * 加载配置（用于扩展加载）
     *
     * @param props 配置地址
     */
    protected void loadAddDo(Properties props, boolean toSystem) {
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
                        if (toSystem) {
                            System.getProperties().put(k1, v1);
                        }

                        put(k1, v1);
                    }
                }
            }
        }
    }
}
