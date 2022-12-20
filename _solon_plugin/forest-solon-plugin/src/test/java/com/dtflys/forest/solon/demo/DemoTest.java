package com.dtflys.forest.solon.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.LoadBalance;
import org.noear.solon.test.SolonJUnit4ClassRunner;

@Component("gitee")
@RunWith(SolonJUnit4ClassRunner.class)
public class DemoTest implements LoadBalance {
    @Inject
    GiteeApi giteeApi;

    @Test
    public void test() {
        String ret = giteeApi.search("solon");
        System.out.println(ret);
        assert ret.contains("html");
    }

    @Override
    public String getServer() {
        return "https://www.gitee.com";
    }
}
