package org.beetl.sql.ext.solon.test.dynamic;


import org.beetl.sql.core.SQLManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.extend.beetlsql.Db;
import org.noear.solon.test.SolonTest;
import org.noear.solon.test.SolonJUnit4ClassRunner;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(DynamicApp.class)
public class DynamicTest {
    @Db
    SQLManager sqlManager;

    @Db
    DynamicUserInfoMapper mapper;

    @Test
    public void test(){
        mapper.deleteById(19999);
        sqlManager.single(UserInfoInDs1.class,1);
        sqlManager.single(UserInfoInDs2.class,1);
        mapper.single(1);
        mapper.queryById(1);
    }
}