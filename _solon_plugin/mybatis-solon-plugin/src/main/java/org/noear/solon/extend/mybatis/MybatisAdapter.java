package org.noear.solon.extend.mybatis;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.core.VarHolder;
import org.noear.solon.extend.mybatis.integration.MybatisMapperInterceptor;

import java.lang.reflect.Proxy;

/**
 * 适配器
 *
 * 1.提供 mapperScan 能力
 * 2.生成 factory 的能力
 *
 * @author noear
 * @since 1.5
 * */
public interface MybatisAdapter {

    /**
     * 获取配置器
     */
    Configuration getConfiguration();

    /**
     * 获取会话工厂
     */
    SqlSessionFactory getFactory();


    /**
     * 打开一个会话
     * */
    default SqlSession openSession(){
        return getFactory().openSession();
    }


    /**
     * 获取印映代理
     * */
    default <T> T getMapper(Class<T> mapperClz) {
        MybatisMapperInterceptor handler = new MybatisMapperInterceptor(getFactory(), mapperClz);

        return (T) Proxy.newProxyInstance(
                mapperClz.getClassLoader(),
                new Class[]{mapperClz},
                handler);
    }


    /**
     * 注入到
     * */
    void injectTo(VarHolder varH);
}
