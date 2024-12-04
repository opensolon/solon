/*
 * Copyright 2017-2024 noear.org and authors
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
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demo6_aop.beans.DnBean;
import webapp.demo6_aop.beans.DsBean;
import webapp.demo6_aop.beans.GtBean;

import java.util.List;
import java.util.Map;

/**
 * @author noear 2023/10/10 created
 */
@SolonTest(App.class)
public class InjectTest2 {
    @Inject
    Map<String, DnBean> dnBeanMap;

    @Inject
    List<DsBean> dsBeanList;

    @Inject
    List<GtBean> gtBeanList;


    @Test
    public void inject_dsBeanList() {
        assert dsBeanList != null;
        assert dsBeanList.size() == 2;
    }

    @Test
    public void inject_dnBeanMap() {
        assert dnBeanMap != null;
        assert dnBeanMap.size() == 2;
        assert dnBeanMap.get("DnBean2") != null;
    }

    @Test
    public void inject_gtBeanList() {
        assert gtBeanList != null;
        assert gtBeanList.size() == 2;
    }
}
