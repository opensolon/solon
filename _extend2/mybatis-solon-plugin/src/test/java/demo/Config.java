package demo;

import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XConfiguration;
import org.noear.solon.annotation.XInject;
import org.noear.solon.extend.mybatis.MybatisAdapter;

import javax.sql.DataSource;


@XConfiguration
public class Config {
    @XBean("sqlSessionFactory1")
    public SqlSessionFactory sqlSessionFactory1(@XInject("dataSource1") DataSource dataSource) throws Exception{
        MybatisAdapter bean = new MybatisAdapter(dataSource);

        return bean.mapperScan("demoapp.model")
                .getFactory();
    }
}
