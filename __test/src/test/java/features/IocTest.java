/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demo0_bean.AopDemoCom2;
import webapp.demo6_aop.Bean2;
import webapp.demo6_aop.DaoUser;
import webapp.demo6_aop.ioc.TestCom1;
import webapp.dso.AutoConfigTest;

@SolonTest(App.class)
//@SolonTest(value = webapp.TestApp.class, args = "-server.port=9001")
public class IocTest {
    @Inject
    Bean2 bean2;

    @Inject("${testpath}")
    String testpath;

    @Inject("${HOME}")
    String java_opts;

    @Inject
    AutoConfigTest autoConfigTest;

    @Inject
    TestCom1 com1;

    @Inject
    DaoUser daoUser;

    @Inject
    AopDemoCom2 aopDemoCom2;

    //双向依赖的bean测试
    //
    @Test
    public void test() {
        assert "bean2bean1".equals(bean2.namePlus());
        assert "D:/abc-1-2.12/xx.xml".equals(testpath);
        System.out.println(testpath);
    }

    @Test
    public void test1() {
        assert "a".equals(autoConfigTest.getUsername());
        Solon.cfg().setProperty("autorefresh.username", "b");
    }

    @Test
    public void test2() {
        assert "b".equals(autoConfigTest.getUsername());
    }

    @Test
    public void test3() {
        assert com1 != null;
    }

    @Test
    public void test4() {
        assert daoUser != null;
        assert daoUser.getClass() != DaoUser.class;
    }

//    @Test
//    public void test5(){
//        aopDemoCom2.test();
//    }

    @Test
    public void evn_inject() {
        System.out.println(System.getenv("HOME"));
        assert java_opts != null;
    }
}
