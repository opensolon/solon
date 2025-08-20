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
package webapp.demo0_bean;

import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Managed;
import org.noear.solon.data.annotation.Tran;

/**
 * @author noear 2023/10/14 created
 */
@Managed
public class AopDemoCom2 {
    @Inject
    AopDemoCom1 demoCom1;

    @Inject
    AopDemoCom2 self;
    @Tran
    public void test(){
        self.test1();
        self.test2();
    }

    public void test1(){
        demoCom1.hello();
    }

    protected void test2(){
        demoCom1.hello();
    }
}
