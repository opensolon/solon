package demo;

import org.apache.ibatis.ext.solon.Db;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.extend.mybatis.MybatisAdapter;

import javax.sql.DataSource;

@Configuration
public class Config {
    @Bean("db1")
    public DataSource sqlSessionFactory1(@Inject("db1") DataSource dataSource) throws Exception {
        return dataSource;
    }

    @Bean("db1")
    public void xxx(@Db("db1") MybatisAdapter adapter) {
        //这是 db1 的配置器//多源时，用这个比较发了
        adapter.getConfiguration();
    }
}
