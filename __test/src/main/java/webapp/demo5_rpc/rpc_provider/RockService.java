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
package webapp.demo5_rpc.rpc_provider;

import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;
import webapp.demo5_rpc.RockApi;
import webapp.models.UserModel;

import java.util.ArrayList;
import java.util.List;

//@Before({SocketChannelAdapter.class})
@Mapping(value = "/demo5/test", method = {MethodType.HTTP, MethodType.SOCKET})
@Remoting
public class RockService implements RockApi {

    @Override
    public Object test1(Integer a) {
        Context ctx = Context.current();

        System.out.println("=============NameContext.Header: user_name: " + ctx.header("user_name"));

        return ctx.method() + "::test1=" + a;
    }

    @Override
    public Object test2(int b){
        return Context.current().path();
    }

    @Override
    public Object test3(){
        throw new RuntimeException("xxxx");
    }

    @Override
    public UserModel test4() {
        UserModel m = new UserModel();
        m.setId(1);
        m.setName("user 1");
        m.setSex(1);
        return m;
    }

    @Override
    public List<UserModel> test5(){
        List<UserModel> list =new ArrayList<>();

        UserModel m = new UserModel();
        m.setId(1);
        m.setName("user 1");
        m.setSex(1);

        list.add(m);

        m = new UserModel();
        m.setId(2);
        m.setName("user 2");
        m.setSex(0);

        list.add(m);


        return list;
    }

    @Override
    public void test6(Integer a) {

    }

    @Override
    public Object testerror() {
        throw new RuntimeException("我出错了");
    }
}
