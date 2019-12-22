package org.noear.solon.core;

import org.noear.solon.ext.Act1;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/** 为 AopFactory 提供存储 支持 */
public class AopFactoryBase {
    //////////////////////////
    //
    // 基础存储
    //
    /////////////////////////

    /** bean包装库 */
    protected final Map<Class<?>, BeanWrap> beanWraps = new ConcurrentHashMap<>();
    /** bean库 */
    protected final Map<String, BeanWrap> beans = new ConcurrentHashMap<>();
    /** clz mapping */
    protected final Map<Class<?>,Class<?>> clzMapping = new ConcurrentHashMap<>();

    //启动时写入
    /** bean loaders */
    protected final Map<Class<?>,BeanLoader<?>> beanLoaders = new HashMap<>();
    /** bean builder */
    protected final Set<BeanBuilder> beanBuilders = new HashSet<>();

    //////////////////////////
    //
    // bean 对外事件存储
    //
    /////////////////////////

    /** bean 加载完成事件 */
    protected final Set<Runnable> loadedEvent = new LinkedHashSet<>();

    //////////////////////////
    //
    // bean 对内通知体系
    //
    /////////////////////////

    /** bean订阅者 */
    private final Map<Object, Set<Act1<BeanWrap>>> _subs = new ConcurrentHashMap<>();

    /** bean订阅 */
    public void beanSubscribe(Object key, Act1<BeanWrap> callback) {
        Set<Act1<BeanWrap>> e = _subs.get(key);
        if (e == null) {
            e = new HashSet<>();
            _subs.put(key, e);
        }

        e.add(callback);
    }

    /** bean通知 */
    public void beanNotice(Object key, BeanWrap wrap) {
        Set<Act1<BeanWrap>> e = _subs.get(key);
        if (e != null) {
            e.forEach(f -> f.run(wrap));
        }
    }
}
