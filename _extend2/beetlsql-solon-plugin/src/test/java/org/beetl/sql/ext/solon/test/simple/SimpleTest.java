package org.beetl.sql.ext.solon.test.simple;

import org.beetl.sql.core.SQLManager;
import org.noear.solon.extend.beetlsql.Db;
import org.beetl.sql.ext.solon.test.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.SolonTest;
import org.noear.solon.test.SolonJUnit4ClassRunner;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(SimpleApp.class)
public class SimpleTest {
    @Db
    SQLManager sqlManager;

    @Db
    SimpleUserInfoMapper userInfoMapper;

    @Test
    public void test(){
        sqlManager.single(UserInfo.class,1);
        userInfoMapper.single(1);
    }
}