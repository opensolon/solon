package webapp.controller;

import org.apache.ibatis.session.SqlSession;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XSingleton;
import webapp.dso.DbUtil;
import webapp.dso.db.AppxMapper;
import webapp.model.AppxModel;

@XMapping("/test")
@XSingleton(true)
@XController
public class TestController {
    @XMapping("demo1")
    public Object test1() throws Exception{
        SqlSession sqlSession = DbUtil.getSqlSession();

        AppxMapper mapper = sqlSession.getMapper(AppxMapper.class);

        AppxModel tmp = mapper.appx_get();

        return tmp;
    }

    @XMapping("demo2")
    public Object test2() throws Exception{
        SqlSession sqlSession = DbUtil.getSqlSession();

        AppxMapper mapper = sqlSession.getMapper(AppxMapper.class);

        AppxModel tmp = mapper.appx_get2(48);

        return tmp;
    }

}
