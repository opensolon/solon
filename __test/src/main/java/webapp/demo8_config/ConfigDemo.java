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
package webapp.demo8_config;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.BindProps;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.Properties;

@Configuration
public class ConfigDemo {
    //注解方式(仅支持简单的类型注入)
    @Inject("${username}")
    public String username;

    @Inject("${paasword}")
    public int paasword;

    @Inject("${demo8.test}")
    public Properties test;

    //获取方式（复杂的请用这种方式）
    //
    public String nameuser_2 = Solon.cfg().get("username");

    public Properties dbcfg = Solon.cfg().getProp("demo8.test");

    public Properties test2(){
        return Solon.cfg().getProp("demo8.test");
    }

    public ConfigDemo(){
        Solon.cfg().onChange((k, v)->{
            if("username".equals(k)){
                username = v;
            }
        });
    }

    @BindProps(prefix = "demo8.test")
    @Managed
    public DsModel2 buildDsModel(){
        return new DsModel2();
    }
}
