package webapp;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.XApp;
import webapp.dso.DbUtil;

public class App {
    public static void main(String[] args){
        XApp.start(App.class,args);

        try {

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private static void xxx() throws Exception{
        SqlSessionFactory sqlSessionFactory = DbUtil.sqlSessionFactory();

        if(sqlSessionFactory!=null){
            SqlSession sqlSession = sqlSessionFactory.openSession();

//            sqlSession.getMapper()
        }
    }
}
