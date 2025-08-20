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
package webapp.demo6_aop.ioc;

import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2023/9/24 created
 */
@Configuration
public class XConfig {
    @Managed
    @Condition(onMissingBean = Service1.class)
    public Service1 service1(Api1 api1, @Inject(required = false) Api0 api0) {
        return new Service1Impl(api1, api0);
    }

    @Managed
    @Condition(onMissingBean = Service2.class)
    public Service2 service2(Api1 api1, Api2 api2, Service1 service1) {
        return new Service2Impl(api1, api2, service1);
    }

    @Managed
    @Condition(onMissingBean = Service3.class)
    public Service3 service3(Api3 api3, @Inject(required = false) Api0 api0, Service1 service1, Service2 service2) {
        return new Service3Impl(api3, api0, service1, service2);
    }

    @Managed
    public TestCom1 com1(Service1 service1, Service2 service2, Service3 service3) {
        return new TestCom1(service1, service2, service3);
    }

    @Managed
    public TestCom2 com2(TestCom1 com1) {
        return new TestCom2();
    }
}
