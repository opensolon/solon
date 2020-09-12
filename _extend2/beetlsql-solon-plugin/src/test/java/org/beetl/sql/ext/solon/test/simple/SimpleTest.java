package org.beetl.sql.ext.solon.test.simple;

import org.noear.solon.annotation.XInject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.SolonTest;
import org.noear.solon.test.SolonJUnit4ClassRunner;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(SimpleApp.class)
public class SimpleTest {
    @XInject
    SimpleService service;

    @Test
    public void test(){
        service.test();
    }
}