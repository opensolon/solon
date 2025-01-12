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

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2023/2/5 created
 */
@Configuration
public class ConditionConfig {
    String username;
    String username2;
    String username3;
    String username4;
    String username5;

    String username11;
    String username12;

    String username21;
    String username22;

    public String getUsername() {
        return username;
    }

    public String getUsername2() {
        return username2;
    }

    public String getUsername3() {
        return username3;
    }

    public String getUsername4() {
        return username4;
    }

    public String getUsername5() {
        return username5;
    }

    public String getUsername11() {
        return username11;
    }

    public String getUsername12() {
        return username12;
    }

    public String getUsername21() {
        return username21;
    }

    public String getUsername22() {
        return username22;
    }

    @Condition(onProperty = "${username}")
    @Bean
    public void setUsername(@Inject("${username}") String u1){
        username = u1;
    }

    @Condition(onProperty = "${username2}")
    @Bean
    public void setUsername2(@Inject("${username2}") String u2){
        username2 = u2;
    }

    @Condition(onProperty = "${username} == noear")
    @Bean
    public void setUsername3(@Inject("${username}") String u1){
        username3 = u1;
    }

    @Condition(onProperty = "username != noear") //语法错误
    @Bean
    public void setUsername4(@Inject("${username}") String u1){
        username4 = u1;
    }

    @Condition(onProperty = "username = noear")
    @Bean
    public void setUsername5(@Inject("${username}") String u1){
        username5 = u1;
    }

    @Condition(onMissingBean = BeanClass1.class)
    @Bean
    public void setUsername11(@Inject("${username}") String u1){
        username11 = u1;
    }

    @Condition(onMissingBean = DemoService.class)
    @Bean
    public void setUsername12(@Inject("${username}") String u1){
        username12 = u1;
    }


    @Condition(onMissingBeanName = "map1_xxx")
    @Bean
    public void setUsername21(@Inject("${username}") String u1){
        username21 = u1;
    }

    @Condition(onMissingBeanName = "map1")
    @Bean
    public void setUsername22(@Inject("${username}") String u1){
        username22 = u1;
    }


}
