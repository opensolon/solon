package org.beetl.sql.ext.solon.test.masterslave;

import org.beetl.sql.core.SQLManager;
import org.noear.solon.extend.beetlsql.Db;
import org.beetl.sql.ext.solon.test.UserInfo;
import org.beetl.sql.ext.solon.test.simple.SimpleApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.SolonTest;
import org.noear.solon.test.SolonJUnit4ClassRunner;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(SimpleApp.class)
public class MasterSlaveTest {
    @Db
    SQLManager sqlManager;

    @Db
    MasterSlaveUserInfoMapper userInfoMapper;

    @Test
    public void test(){
        userInfoMapper.deleteById(19999);
        sqlManager.single(UserInfo.class,1);
        userInfoMapper.single(1);
    }
}