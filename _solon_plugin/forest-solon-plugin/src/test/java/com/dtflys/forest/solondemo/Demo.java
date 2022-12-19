package com.dtflys.forest.solondemo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;

@RunWith(SolonJUnit4ClassRunner.class)
public class Demo {
    @Inject
    TestGiteeApi api;

    @Test
    public void test() {
        String ret = api.search("solon");
        System.out.println(ret);
        assert ret.contains("html");
    }
}
