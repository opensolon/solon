package org.noear.solon.extend.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

import java.io.Reader;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {

        try {
            //通过配置文件获取数据库连接信息
            Reader reader = Resources.getResourceAsReader("mybatis.xml");

            //通过配置信息构建一个SqlSessionFactory
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

            //通过SqlSessionFactory打开一个数据库会话
            SqlSession sqlsession = sqlSessionFactory.openSession();

            Aop.put(SqlSessionFactory.class, sqlSessionFactory);
            Aop.put("sqlsession", sqlsession);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
