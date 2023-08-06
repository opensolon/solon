package demo;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.solon.Mybatis;

/**
 * @author noear 2022/11/22 created
 */
public class DemoService {
    public void demo() {
        //手动获取 mapper
        UserMapper tmp = Mybatis.use("db1").getMapper(UserMapper.class);

        //手动拿session
        SqlSession session = Mybatis.use("db2").openSession();
    }
}
