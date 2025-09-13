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
package features.serialization.snack3.test1_2;

import features.serialization.snack3.model.CustomDateDo;
import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.serialization.snack3.SnackRenderFactory;
import org.noear.solon.test.SolonTest;

import java.util.Date;

/**
 * 时间进行格式化 + long,int 转为字符串 + 常见类型转为非null + 所有null输出
 */
@Import(profiles = "classpath:features2_test1-2.yml")
@SolonTest
public class TestQuickConfig {
    @Inject
    SnackRenderFactory renderFactory;

    @Test
    public void hello2() throws Throwable{
        CustomDateDo dateDo = new CustomDateDo();

        dateDo.setDate(new Date(1673861993477L));
        dateDo.setDate2(new Date(1673861993477L));

        ContextEmpty ctx = new ContextEmpty();
        renderFactory.create().render(dateDo, ctx);
        String output = ctx.attr("output");

        System.out.println(output);

        assert "{\"date\":1673861993477,\"date2\":\"2023-01-16\"}".equals(output);
    }
}
