package webapp.controller;

import org.apache.ibatis.session.SqlSession;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XSingleton;
import webapp.dso.DbUtil;
import webapp.dso.db.DbMapper;

@XMapping("/anno")
@XSingleton(true)
@XController
public class AnnoController {

    @XMapping("/demo1/json")
    public Object demo1() throws Exception {
        SqlSession session = DbUtil.sqlSessionFactory().openSession();

        return session.getMapper(DbMapper.class).appx_get();
    }
}
