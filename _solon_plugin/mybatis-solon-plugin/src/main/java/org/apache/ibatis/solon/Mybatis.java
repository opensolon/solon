package org.apache.ibatis.solon;

import org.apache.ibatis.solon.integration.MybatisAdapterManager;

/**
 * Mybatis 手动使用接口
 *
 * @author
 * @since 1.10
 */
public interface Mybatis {
    /**
     * 获取源
     */
    static MybatisAdapter use(String name) {
        return MybatisAdapterManager.getOnly(name);
    }
}
