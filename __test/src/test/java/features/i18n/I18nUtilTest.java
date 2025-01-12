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
package features.i18n;

import org.junit.jupiter.api.Test;
import org.noear.solon.i18n.I18nBundle;
import org.noear.solon.i18n.I18nService;
import org.noear.solon.i18n.I18nUtil;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.util.List;
import java.util.Locale;

/**
 * @author noear 2021/9/19 created
 */
@SolonTest(App.class)
public class I18nUtilTest {
    @Test
    public void test() {
        assert "登录".equals(I18nUtil.getMessage(Locale.CHINA, "login.title"));
        assert "login-us".equals(I18nUtil.getMessage(Locale.US, "login.title"));
    }

    @Test
    public void test1() {
        I18nBundle bundle = I18nUtil.getBundle(I18nUtil.getMessageBundleName(), Locale.CHINA);
        assert "登录".equals(bundle.get("login.title"));
    }

    I18nService service = new I18nService(I18nUtil.getMessageBundleName());

    @Test
    public void test2() {
        assert "登录".equals(service.get(Locale.CHINA, "login.title"));
        assert "login-us".equals(service.get(Locale.US, "login.title"));
    }

    @Test
    public void test3() {
        assert "xxx".equals(service.get(Locale.CHINA, "login.name"));
        assert "登录".equals(service.get(Locale.JAPAN, "login.title"));
    }

    @Test
    public void test4(){
         List list = service.toProps(Locale.CHINA).toBean("site.urls", List.class);

         assert list!=null;
         assert list.size() == 2;
    }
}
