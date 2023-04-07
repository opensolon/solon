package demo;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author noear 2023/4/7 created
 */
public class App {
    public static void main(String... args) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/mybatis-flex");
        dataSource.setUsername("username");
        dataSource.setPassword("password");

        MybatisFlexBootstrap.getInstance()
                .setDataSource(dataSource)
                .start();

//        AccountMapper mapper = MybatisFlexBootstrap.getInstance()
//                .getMapper(AccountMapper.class);


        //示例1：查询 id=100 条数据
        //Account account = mapper.selectOneById(100);
    }
}
