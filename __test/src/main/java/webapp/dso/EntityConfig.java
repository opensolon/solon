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
package webapp.dso;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Map;

/**
 * @author noear 2021/9/27 created
 */
@Inject("${app.dict}")
@Configuration
public class EntityConfig {
    public String name;
    public Integer age;
    //@Inject("${app.dict.codes}")//不需要分块注入了
    public Map<String,String> codes;
    //@Inject("${app.dict.likes}")//不需要分块注入了
    public List<String> likes;

    @Override
    public String toString() {
        return "EntityConfig{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", codes=" + codes +
                ", likes=" + likes +
                '}';
    }


    @Init
    public void init(){
        System.out.println("我是5号");
    }
}
