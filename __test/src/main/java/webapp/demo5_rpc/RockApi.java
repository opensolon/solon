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
package webapp.demo5_rpc;

import org.noear.nami.annotation.NamiClient;
import webapp.models.UserModel;

import java.util.List;

@NamiClient(name = "demo",path = "/demo5/test/", timeout = 20)
public interface RockApi {
    Object test1(Integer a);
    Object test2(int b);
    Object test3();
    UserModel test4();
    List<UserModel> test5();
    void test6(Integer a);

    Object testerror();

    default Object textdef(){
        return test3();
    }
}