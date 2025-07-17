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
package features.validation;

import org.junit.jupiter.api.Test;
import org.noear.solon.validation.ValidUtils;
import org.noear.solon.validation.annotation.Email;
import org.noear.solon.validation.annotation.EmailValidator;

import java.util.regex.Pattern;

public class ExprTest {
    @Test
    public void email() {
        Pattern pp = Pattern.compile(EmailValidator.DEFAULT_PATTERN);

        assert pp.matcher("service@xsoftlab.net").find();
        assert pp.matcher("noear@live.com.cn").find();
        assert pp.matcher("noe120ar@li0ve.com.cn").find();
        assert pp.matcher("noe.120ar@163.com.cn").find();
        assert pp.matcher("12.33@163.com.cn").find();
        assert pp.matcher("db_db@li0ve.com.cn").find();
        assert pp.matcher("as-df@li0ve.com.cn").find();
        assert pp.matcher("as-df@li-ve.com.cn").find();
    }

    @Test
    public void email2() {
        ValidUtils.validateEntity(new DemoObj("service@xsoftlab.net"));
        ValidUtils.validateEntity(new DemoObj("noear@live.com.cn"));
        ValidUtils.validateEntity(new DemoObj("noe120ar@li0ve.com.cn"));
        ValidUtils.validateEntity(new DemoObj("noe.120ar@163.com.cn"));
        ValidUtils.validateEntity(new DemoObj("12.33@163.com.cn"));
        ValidUtils.validateEntity(new DemoObj("db_db@li0ve.com.cn"));
        ValidUtils.validateEntity(new DemoObj("as-df@li0ve.com.cn"));
        ValidUtils.validateEntity(new DemoObj("as-df@li-ve.com.cn"));
    }

    public static class DemoObj {
        @Email
        public final String email;

        public DemoObj(String email) {
            this.email = email;
        }
    }
}