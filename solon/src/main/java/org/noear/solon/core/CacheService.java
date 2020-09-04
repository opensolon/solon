package org.noear.solon.core;

public interface CacheService {
    /** 保存 */
    void store(String key, Object obj, int seconds);
    /** 获取 */
    Object get(String key);
    /** 移除 */
    void remove(String key);
}
