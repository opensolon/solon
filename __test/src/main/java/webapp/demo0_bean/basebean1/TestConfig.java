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
package webapp.demo0_bean.basebean1;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2024/8/27 created
 */
@Configuration
public class TestConfig {
    @Bean(name = "a", typed = true)
    public A a() { //只注册实例和返回类型的，一级上层接口
        return new A() {
            @Override
            public void hello() {
                System.out.println("basebean1:A: hello");
            }
        };
    }

    @Inject
    B b; // C b 时，会出错

    @Init
    public void init(){
        b.hello();
    }
}
