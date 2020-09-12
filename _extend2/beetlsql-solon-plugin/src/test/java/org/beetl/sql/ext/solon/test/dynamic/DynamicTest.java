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
    DynamicService single;

    @Test
    public void test(){
        single.test();
    }
}