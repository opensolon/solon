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

import demo.dso.UserMapper;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.datasource.DsUtils;
import org.noear.solon.data.dynamicds.DynamicDataSource;
import org.noear.solon.data.dynamicds.DynamicDs;
import org.noear.solon.annotation.Component;
import org.noear.solon.data.dynamicds.DynamicDsKey;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

/**
 * @author noear 2022/11/23 created
 */

public class DemoDsTest {
    @Configuration
    public class Config {
        @Bean("db_user")
        public DataSource dsUser(@Inject("$demo.ds.db_user}") DynamicDataSource dataSource) {
            return dataSource;
        }

        //@Bean("db_user")
        public DataSource dsUser2(@Inject("$demo.ds.db_user}") Properties props) {
            //手动构建，可以不用配置：type, strict
            Map<String, DataSource> dsMap = DsUtils.buildDsMap(props, DataSource.class);
            DataSource dsDef = dsMap.get("default");

            DynamicDataSource tmp = new DynamicDataSource();
            tmp.setStrict(true);
            tmp.setTargetDataSources(dsMap);
            tmp.setDefaultTargetDataSource(dsDef);

            return tmp;
        }
    }

    @Component
    public class UserService {
        @Db("db_user")
        UserMapper userMapper;

        @DynamicDs //使用默认源
        public void addUser() {
            userMapper.inserUser();
        }

        @DynamicDs("db_user_1") //使用 db_user_1 源
        public void getUserList() {
            userMapper.selectUserList();
        }

        public void getUserList2() {
            DynamicDsKey.setCurrent("db_user_2"); //使用 db_user_2 源
            try {
                userMapper.selectUserList();
            } finally {
                DynamicDsKey.remove();
            }
        }
    }
}