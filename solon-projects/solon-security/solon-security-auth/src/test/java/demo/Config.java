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
package demo;

import demo.dso.AuthProcessorImpl;
import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.auth.AuthAdapter;

/**
 * @author noear 2021/6/2 created
 */
@Configuration
public class Config {
    /**
     * 单验证体系
     */
    @Managed
    public AuthAdapter authAdapter1() {
        return new AuthAdapter()
                .loginUrl("/login") //设定登录地址，未登录时自动跳转
                .addRule(r -> r.include("**").verifyIp().failure((c, t) -> c.output("你的IP不在白名单"))) //添加规则
                .addRule(b -> b.exclude("/login**").exclude("/_run/**").verifyPath()) //添加规则
                .processor(new AuthProcessorImpl()) //设定认证处理器
                .failure((ctx, rst) -> { //设定默认的验证失败处理
                    ctx.render(rst);
                });
    }
}
