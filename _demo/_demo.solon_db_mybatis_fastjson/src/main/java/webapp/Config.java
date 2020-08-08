package webapp;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XConfiguration;
import org.noear.solon.annotation.XInject;
import org.noear.solon.extend.mybatis.MapperScan;
import org.noear.solon.extend.mybatis.MybatisAdapter;

import java.util.Properties;

@MapperScan(basePackages = "webapp.dso.db", sqlSessionFactoryRef = "sqlSessionFactory1")
@XConfiguration
public class Config {
    /**
     * 使用 xml 配置创建
     */
    @XBean("sqlSessionFactory1")
    public SqlSessionFactory sqlSessionFactory1(
            @XInject("${test.db1}") HikariDataSource dataSource,
            @XInject("${mybatis.case1}") Properties props) {
        return new MybatisAdapter(dataSource, props)
                .getFactory();
    }
}
