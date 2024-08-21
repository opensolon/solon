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
package demo2.dso;

import org.noear.solon.annotation.Component;
import org.noear.solon.auth.AuthAdapter;
import org.noear.solon.auth.AuthAdapterSupplier;

/**
 * @author noear 2022/8/15 created
 */
@Component
public class UserAuthAdapterSupplierImpl implements AuthAdapterSupplier {
    final AuthAdapter userAuth;

    public UserAuthAdapterSupplierImpl(){
        userAuth = new AuthAdapter()
                .loginUrl("/user/login") //设定登录地址，未登录时自动跳转
                .addRule(b -> b.include("/user/**").exclude("/user/login**").verifyPath()) //添加规则
                .processor(new UserAuthProcessorImpl()) //设定认证处理器
                .failure((ctx, rst) -> { //设定默认的验证失败处理
                    ctx.render(rst);
                });
    }

    @Override
    public String pathPrefix() {
        return "/user/";
    }

    public AuthAdapter adapter(){
        return userAuth;
    }
}