package demo;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XConfiguration;
import org.noear.solon.annotation.XInject;

import javax.sql.DataSource;

@XConfiguration
public class Config {
    @XBean("db1")
    public DataSource sqlSessionFactory1(@XInject("db1") DataSource dataSource) throws Exception{
       return dataSource;
    }
}
