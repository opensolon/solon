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
package webapp.demo0_bean.construction1;

import org.noear.solon.annotation.Managed;

/**
 * @author noear 2024/8/11 created
 */
@Managed
public class Construction1Com11B2 {
    private Condition1B condition1B;

    public Construction1Com11B2(Condition1B condition1B) {
        this.condition1B = condition1B;
    }

    public Condition1B getCondition1B() {
        return condition1B;
    }
}
