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
package features.org.noear.solon.vault;

import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.vault.VaultUtils;

import java.util.Properties;

/**
 * @author noear 2022/7/5 created
 */
public class ConfigVaultTest {
    @Test
    public void test() {
        Solon.start(ConfigVaultTest.class, new String[]{}, app -> {
            app.cfg().loadAdd("test1.yml");
        });

        String password1 = Solon.cfg().get("test.password1");
        String password2 = VaultUtils.guard(Solon.cfg().get("test.password2"));

        Properties db1_props = Solon.cfg().getProp("test.db1");
        Properties db2_props = VaultUtils.guard(Solon.cfg().getProp("test.db2"));

        System.out.println(password1);
        System.out.println(password2);
        System.out.println(db1_props);
        System.out.println(db2_props);

        assert password1.equals(password2);
        assert db1_props.get("password").equals(db2_props.get("password"));
        assert db1_props.get("username").equals(db2_props.get("username"));
        assert db1_props.get("url").equals(db2_props.get("url"));
    }

    @Test
    public void test2() {
        Solon.start(ConfigVaultTest.class, new String[]{});

        String password2 = VaultUtils.guard("dddd");

        System.out.println(password2);

        assert password2.equals("dddd");
    }
}
