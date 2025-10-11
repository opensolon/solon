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

/**
 * @author noear 2024/8/11 created
 */
//@Component
public class Construction1Com11A {
    private final Condition1A condition1A;

    public Construction1Com11A(Condition1A condition1A) {
        this.condition1A = condition1A;
    }

    public Condition1A getCondition1A() {
        return condition1A;
    }
}
