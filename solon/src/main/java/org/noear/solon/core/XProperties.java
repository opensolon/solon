package org.noear.solon.core;

import org.noear.solon.XUtil;

import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 通用属性集合
 *
 * 在 Properties 基础上，添加了些方法
 *
 * @author noear
 * @since 1.0
 * */
public class XProperties extends Properties {

    public XProperties() {
        super();
    }

    public XProperties(Properties defaults) {
        super(defaults);
    }

    /**
     * 获取某项配置
     */
    public String get(String key) {
        return getProperty(key);
    }

    public String get(String key, String def) {
        return getProperty(key, def);
    }

    public boolean getBool(String key, boolean def) {
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

    private <T> T getOrDef(String key, T def, Function<String, T> convert) {
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return def;
        } else {
            return convert.apply(temp);
        }
    }



    /**
     * 查找 keyStarts 开头的所有配置；并生成一个新的 Bean
     *
     * @param keyStarts key 的开始字符
     * */
    public <T> T getBean(String keyStarts, Class<T> clz) {
        return ClassWrap.get(clz).newBy(getProp(keyStarts)::getProperty);
    }

    /**
     * 查找 keyStarts 开头的所有配置；并生成一个新的 配置集
     *
     * @param keyStarts key 的开始字符
     * */
    public XProperties getProp(String keyStarts) {
        XProperties prop = new XProperties();
        findDo(keyStarts, prop::put);
        return prop;
    }

    /**
     *
     * @param expr 兼容 ${key} or key
     * */
    public XProperties getPropByExpr(String expr) {
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
     * */
    public XMap getXmap(String keyStarts) {
        XMap map = new XMap();
        findDo(keyStarts, map::put);
        return map;
    }

    private void findDo(String keyStarts, BiConsumer<String, String> setFun) {
        String key2 = keyStarts + ".";
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
                            if(ss[i].length()>1) {
                                sb.append(ss[i].substring(0, 1).toUpperCase()).append(ss[i].substring(1));
                            }else{
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
     * */
    @Override
    public synchronized void forEach(BiConsumer<? super Object, ? super Object> action) {
        if (defaults != null) {
            defaults.forEach(action);
        }

        super.forEach(action);
    }
}
