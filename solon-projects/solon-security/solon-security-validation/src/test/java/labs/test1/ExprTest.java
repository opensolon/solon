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
package labs.test1;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class ExprTest {
    @Test
    public void test(){
        Pattern pp= Pattern.compile("^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\\.][a-z]{2,3}([\\.][a-z]{2})?$");

        assert pp.matcher("service@xsoftlab.net").find();
        assert pp.matcher("noear@live.com.cn").find();
        assert pp.matcher("noe120ar@li0ve.com.cn").find();
        assert pp.matcher("db_db@li0ve.com.cn").find();
        assert pp.matcher("as-df@li0ve.com.cn").find();
        assert pp.matcher("as-df@li-ve.com.cn").find();
    }
}
