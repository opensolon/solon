package org.noear.solon.data.cache;

/**
 * 缓存服务接口（用于支持@Cache相关注解）
 *
 * @author noear
 * @since 1.0
 * */
public interface CacheService {
    /**
     * 保存
     */
    void store(String key, Object obj, int seconds);

    /**
     * 获取
     */
    Object get(String key);

    /**
     * 移除
     */
    void remove(String key);


    /**
     * 缓存标签管理器
     * */
    default CacheTags tags(){
        return new CacheTags(this);
    }
}
