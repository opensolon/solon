/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.core;

import org.noear.solon.SolonProps;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Import;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.PropUtil;
import org.noear.solon.core.util.ResourceUtil;

import java.net.URL;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
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
    private Map<String, String> tempPropMap = new TreeMap<>();
    private ReentrantLock SYNC_LOCK = new ReentrantLock();

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

    /**
     * @see Props:addAll
     * @deprecated 3.0
     */
    @Deprecated
    public Props(Map<String, String> data) {
        super();
        super.putAll(data);
    }

    /**
     * @see Props:addAll
     * @deprecated 3.0
     */
    @Deprecated
    public Props(Iterable<KeyValues<String>> data) {
        super();
        for (KeyValues<String> kv : data) {
            super.put(kv.getKey(), kv.getFirstValue());
        }
    }

    @Override
    public int size() {
        SYNC_LOCK.lock();

        try {
            if (defaults == null) {
                return super.size();
            } else {
                return super.size() + defaults.size();
            }
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 获取属性
     */
    public String get(String key) {
        return getProperty(key);
    }

    public String getByKeys(String... keys) {
        for (String key : keys) {
            String tmp = get(key);
            if (Utils.isNotEmpty(tmp)) {
                return tmp;
            }
        }

        return null;
    }

    /**
     * @param expr 兼容 ${key} or key or ${key:def} or key:def
     */
    public String getByExpr(String expr) {
        return getByExpr(expr, null, null);
    }

    /**
     * @param expr 兼容 ${key} or key or ${key:def} or key:def
     */
    protected String getByExpr(String expr, Properties props, String refKey) {
        return PropUtil.getByExp(this, props, expr, refKey);
    }

    /**
     * @param tmpl 模板： ${key} 或 aaa${key}bbb 或 ${key:def}/ccc
     */
    public String getByTmpl(String tmpl) {
        return getByTmpl(tmpl, null, null);
    }

    /**
     * @param tmpl 模板： ${key} 或 aaa${key}bbb 或 ${key:def}/ccc
     */
    protected String getByTmpl(String tmpl, Properties props, String refKey) {
        return PropUtil.getByTml(this, props, tmpl, refKey);
    }

    /**
     * @param tml    模板： ${key} 或 aaa${key}bbb 或 ${key:def}/ccc
     * @param useDef 是否使用默认值
     */
    protected String getByTmpl(String tml, Properties props, String refKey, boolean useDef) {
        return PropUtil.getByTml(this, props, tml, refKey, useDef);
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
        return getOrDefault(key, def, Boolean::parseBoolean);
    }

    /**
     * 获取某项配置，并转为整型（如果没有，输出默认值）
     *
     * @param def 默认值
     */
    public int getInt(String key, int def) {
        return getOrDefault(key, def, Integer::parseInt);
    }

    /**
     * 获取某项配置，并转为长整型（如果没有，输出默认值）
     *
     * @param def 默认值
     */
    public long getLong(String key, long def) {
        return getOrDefault(key, def, Long::parseLong);
    }

    /**
     * 获取某项配置，并转为又精度型（如果没有，输出默认值）
     *
     * @param def 默认值
     */
    public Double getDouble(String key, double def) {
        return getOrDefault(key, def, Double::parseDouble);
    }

    /**
     * 获取某项配置，并转为目标类型（如果没有，输出默认值）
     */
    public <T> T getOrDefault(String key, T def, Function<String, T> convert) {
        String temp = get(key);
        if (Utils.isEmpty(temp)) {
            return def;
        } else {
            return convert.apply(temp);
        }
    }


    /**
     * 查找 keyStarts 开头的所有配置；转为换一个类实例
     *
     * @param keyStarts key 的开始字符
     * @deprecated 2.9
     */
    @Deprecated
    public <T> T getBean(String keyStarts, Class<T> clz) {
        return toBean(keyStarts, clz);
    }

    /**
     * 转为换一个类实例
     *
     * @deprecated 2.9
     */
    @Deprecated
    public <T> T getBean(Class<T> clz) {
        return toBean(clz);
    }

    /**
     * 查找 keyStarts 开头的所有配置；并转为换一个类实例
     *
     * @param keyStarts key 的开始字符
     * @since 2.9
     */
    public <T> T toBean(String keyStarts, Class<T> clz) {
        Properties props = getProp(keyStarts);
        return PropsConverter.global().convert(props, clz);
    }

    /**
     * 转为换一个类实例
     *
     * @since 2.9
     */
    public <T> T toBean(Class<T> clz) {
        return PropsConverter.global().convert(this, clz);
    }

    /**
     * 绑定到一个类实例上
     */
    public <T> T bindTo(T obj) {
        if (this.size() > 0) {
            PropsConverter.global().convert(this, obj, null, null);
        }
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
                }
                prop.put(key, val);
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

        Map<String, Props> groupProps = new LinkedHashMap<>();

        for (String group : groups) {
            Props tmp = rootProps.getProp(group);
            groupProps.put(group, tmp);
        }

        return groupProps;
    }

    /**
     * 查找 keyStarts 开头的所有配置；并生成一个新的列表的配置集
     *
     * @param keyStarts key 的开始字符
     */
    public Collection<Props> getListedProp(String keyStarts) {
        Props rootProps = getProp(keyStarts);

        Set<String> groups = new HashSet<>();
        for (Object key : rootProps.keySet()) {
            if (key instanceof String) {
                groups.add(((String) key).split("\\.")[0]);
            }
        }

        Map<String, Props> groupProps = new LinkedHashMap<>();

        for (String group : groups) {
            Props tmp = rootProps.getProp(group);
            groupProps.put(group, tmp);
        }

        return groupProps.values();
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
    public Map<String, String> getMap(String keyStarts) {
        Map<String, String> map = new LinkedHashMap<>();
        doFind(keyStarts, (key, val) -> { //相对旧版，减少一次 forEach
            if (key.startsWith(".")) {
                key = key.substring(1); //去掉 .
            }
            map.put(key, val);
        });
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

    protected void doFind(String keyStarts, BiConsumer<String, String> setFun) {
        String key2 = keyStarts;
        int idx2 = key2.length();

        forEach((k, v) -> {
            if (k instanceof String && v instanceof String) {
                String keyStr = (String) k;

                if (keyStr.startsWith(key2)) {
                    String key = keyStr.substring(idx2);

                    setFun.accept(key, (String) v);

                    if (key.indexOf('-') >= 0) {
                        String camelKey = Utils.snakeToCamel(key);
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
    public void forEach(BiConsumer<? super Object, ? super Object> action) {
        SYNC_LOCK.lock();

        try {
            super.forEach(action);

            if (defaults != null) {
                defaults.forEach((k, v) -> {
                    if (super.containsKey(k) == false) {
                        action.accept(k, v);
                    }
                });
            }
        } finally {
            SYNC_LOCK.unlock();
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
    public Object put(Object key, Object value) {
        SYNC_LOCK.lock();

        try {
            Object obj = super.put(key, value);

            if (key instanceof String && value instanceof String) {
                _changeEvent.forEach(event -> {
                    event.accept((String) key, (String) value);
                });
            }

            return obj;
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    public void putIfNotNull(Object key, Object value) {
        if (key != null && value != null) {
            this.put(key, value);
        }
    }

    ////

    /**
     * 添加所有属性数据
     *
     * @since 3.0
     */
    public Props addAll(Properties data) {
        if (data != null) {
            super.putAll(data);
        }
        return this;
    }

    /**
     * 添加所有属性数据
     *
     * @since 3.0
     */
    public Props addAll(Map<String, String> data) {
        if (data != null) {
            super.putAll(data);
        }
        return this;
    }

    /**
     * 添加所有属性数据
     *
     * @since 3.0
     */
    public Props addAll(Iterable<KeyValues<String>> data) {
        if (data != null) {
            for (KeyValues<String> kv : data) {
                super.put(kv.getKey(), kv.getFirstValue());
            }
        }
        return this;
    }

    ////

    /**
     * 加载配置（用于扩展加载）
     *
     * @param uri 资源地址（"classpath:demo.xxx" or "file:./demo.xxx" or "./demo.xxx"）
     */
    public void loadAdd(String uri) {
        loadAdd(ResourceUtil.findResource(uri, false));
    }

    public void loadAdd(Import anno) {
        if (anno == null) {
            return;
        }

        for (String uri : anno.profiles()) {
            uri = getByTmpl(uri);
            loadAdd(ResourceUtil.findResource(classLoader, uri));
        }

        for (String uri : anno.profilesIfAbsent()) {
            uri = getByTmpl(uri);
            loadAddIfAbsent(ResourceUtil.findResource(classLoader, uri));
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


    protected void loadAddDo(Properties props, boolean toSystem, boolean addIfAbsent) {
        //加载配置
        this.loadAddDo(props, toSystem, addIfAbsent, false);
        //校正配置
        this.reviseDo(false);
    }

    /**
     * 加载配置（用于扩展加载）
     *
     * @param props 配置地址
     */
    protected void loadAddDo(Properties props, boolean toSystem, boolean addIfAbsent, boolean isEnd) {
        if (props != null) {
            for (Map.Entry<Object, Object> kv : props.entrySet()) {
                Object k1 = kv.getKey();
                Object v1 = kv.getValue();

                if (addIfAbsent) {
                    //如果已存在，则不盖掉
                    if (containsKey(k1)) {
                        tempPropMap.remove(k1);
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
                        // db1.jdbcUrl=jdbc:mysql:${db1.server}/${db1.db:order}
                        String valExp = (String) v1;
                        v1 = getByTmpl(valExp, props, key, isEnd);

                        if (v1 == null) {
                            if (!isEnd) {
                                tempPropMap.put(key, valExp);
                            }
                        } else {
                            //如果加载成功且存在于列表中，从变量中移除
                            tempPropMap.remove(key);
                        }
                    }

                    if (v1 != null) {
                        put(k1, v1);

                        if (key.indexOf('-') < 0) {
                            if (toSystem) {
                                //带 - 的不同步到 System
                                System.getProperties().put(k1, v1);
                            }
                        } else {
                            String camelKey = Utils.snakeToCamel(key);
                            if (addIfAbsent) {
                                putIfAbsent(camelKey, v1);
                            } else {
                                put(camelKey, v1);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 完成
     */
    public void complete() {
        reviseDo(true);
    }

    /**
     * 校正（多文件加载后）
     */
    protected void reviseDo(boolean isEnd) {
        //如果加载完成还存在变量，则特殊处理
        if (tempPropMap.size() == 0) {
            return;
        }

        Properties tempProps = new Properties();
        tempProps.putAll(tempPropMap);
        this.loadAddDo(tempProps, false, isEnd, isEnd); //中间可能会有 put 进来，不能再盖掉

        //如果还存在遗留项则抛出异常
        if (isEnd && tempPropMap.size() > 0) {
            throw new IllegalStateException("Config verification failed: " + tempPropMap);
        }
    }
}
