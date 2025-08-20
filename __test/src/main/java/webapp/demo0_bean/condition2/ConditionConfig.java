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
package webapp.demo0_bean.condition2;

import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;

/**
 * @author noear 2024/8/7 created
 */
@Configuration
public class ConditionConfig {
    @Condition(onBean = Condition2A.class)
    @Managed
    public ConditionBean11A ConditionBean11A() {
        return new ConditionBean11A();
    }

    @Condition(onBeanName = "Condition2A")
    @Managed
    public ConditionBean12A ConditionBean12A() {
        return new ConditionBean12A();
    }


    @Condition(onBean = Condition2B.class)
    @Managed
    public ConditionBean11B ConditionBean11B() {
        return new ConditionBean11B();
    }

    @Condition(onBeanName = "Condition2B")
    @Managed
    public ConditionBean12B ConditionBean12B() {
        return new ConditionBean12B();
    }
}
