package org.noear.solon.extend.mybatis;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.extend.mybatis.integration.SqlSessionProxy;

/**
 * 适配器
 *
 * 1.提供 mapperScan 能力
 * 2.生成 factory 的能力
 *
 * @author noear
 * @since 1.5
 * */
public interface SqlAdapter {
    /**
     * 获取配置器
     * */
     Configuration getConfig();

    /**
     * 获取会话工厂
     * */
     SqlSessionFactory getFactory() ;

    /**
     * 替代 @mapperScan
     */
     SqlAdapter mapperScan(SqlSessionProxy proxy);
}
