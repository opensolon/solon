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
package features.core;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demo0_bean.condition1.ConditionCom11A;
import webapp.demo0_bean.condition1.ConditionCom11B;
import webapp.demo0_bean.condition1.ConditionCom12A;
import webapp.demo0_bean.condition1.ConditionCom12B;
import webapp.demo0_bean.condition2.ConditionBean11A;
import webapp.demo0_bean.condition2.ConditionBean11B;
import webapp.demo0_bean.condition2.ConditionBean12A;
import webapp.demo0_bean.condition2.ConditionBean12B;
import webapp.demo0_bean.construction1.Construction1Com11B;
import webapp.demo0_bean.construction1.Construction1Com11B2;
import webapp.dso.ConditionConfig;
import webapp.models.BaseRequest;

/**
 * @author noear 2023/2/5 created
 */
@Configuration
@SolonTest(App.class)
public class ConditionTest {

    @Inject
    ConditionConfig config;

    @Inject
    BaseRequest baseRequest;

    @Test
    public void test() {
        assert config.getUsername() != null;
        assert config.getUsername2() == null;
        assert "noear".equals(config.getUsername3());
        assert config.getUsername4() == null;
        assert "noear".equals(config.getUsername5());

        assert "noear".equals(config.getUsername11());
        assert config.getUsername12() == null;

        assert "noear".equals(config.getUsername21());
        assert config.getUsername22() == null;

        System.out.println(ONode.ofBean(config, Feature.Write_Nulls).toJson());

        assert baseRequest != null;
        System.out.println(ONode.ofBean(baseRequest).toJson());
    }

    @Test
    public void test2() {
        assert Solon.context().getBean(ConditionCom11A.class) == null;
        assert Solon.context().getBean(ConditionCom12A.class) == null;

        assert Solon.context().getBean(ConditionCom11B.class) != null;
        assert Solon.context().getBean(ConditionCom12B.class) != null;
    }

    @Test
    public void test3() {
        assert Solon.context().getBean(ConditionBean11A.class) == null;
        assert Solon.context().getBean(ConditionBean12A.class) == null;

        assert Solon.context().getBean(ConditionBean11B.class) != null;
        assert Solon.context().getBean(ConditionBean12B.class) != null;
    }

    @Test
    public void test4(){
        assert Solon.context().getBean(Construction1Com11B.class) != null;
        assert Solon.context().getBean(Construction1Com11B2.class) != null;
    }
}
