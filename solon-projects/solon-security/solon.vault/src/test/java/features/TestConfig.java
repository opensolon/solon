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
package features;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.vault.annotation.VaultInject;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author noear 2022/7/5 created
 */
@Configuration
public class TestConfig {
    @Inject("${test.password1}")
    private String password1;

    @VaultInject("${test.password2}")
    private String password2;

    @Bean("db1")
    private Properties db1(@Inject("${test.db1}") Properties props){
        System.out.println(password1);
        System.out.println(password2);
        System.out.println(props);

        return props;
    }

    @Bean("db2")
    private Properties db2(@VaultInject("${test.db2}") Properties props){
        System.out.println(password1);
        System.out.println(password2);
        System.out.println(props);

        return props;
    }

    @Bean("db3")
    private DataSource db3(@VaultInject("${test.db2}") HikariDataSource ds){
        return ds;
    }
}
