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
package features;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.noear.solon.test.SolonTest;
import org.noear.solon.vault.annotation.VaultInject;
import webapp.App;

/**
 * @author noear 2022/9/24 created
 */
@SolonTest(App.class)
public class VaultTest {
    @VaultInject("${vault.test.db1}")
    HikariDataSource dsTmp;

    @VaultInject("${vault.test.db1.password}")
    private String password2;

    @Test
    public void test(){
        assert "root".equals(dsTmp.getUsername());
        assert "123456".equals(dsTmp.getPassword());
        assert "123456".equals(password2);
    }
}
