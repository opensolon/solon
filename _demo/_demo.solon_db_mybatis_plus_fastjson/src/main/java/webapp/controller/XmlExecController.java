package webapp.controller;

import org.apache.ibatis.session.SqlSession;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XSingleton;
import webapp.dso.DbUtil;

@XMapping("/xmlexc")
@XSingleton(true)
@XController
public class XmlExecController {

    @XMapping("/demo1/json")
    public Object demo1() throws Exception {
        SqlSession session = DbUtil.sqlSessionFactory().openSession();
        return session.selectOne("webapp.dso.db.appx_get2", 48);
    }
}
