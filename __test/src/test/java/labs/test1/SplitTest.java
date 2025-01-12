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

import java.util.Arrays;
import java.util.Locale;

/**
 * @author noear 2021/11/13 created
 */
public class SplitTest {
    @Test
    public void test() {
        String[] ss;

        ss = "zh_CN".split("_|-");
        System.out.println(Arrays.asList(ss));

        ss = "zh-CN".split("_|-");
        System.out.println(Arrays.asList(ss));

        ss = "zh_CN-HANS".split("_|-");
        System.out.println(Arrays.asList(ss));
    }

    @Test
    public void test2() {
        String[] ss;

        ss = "zh".split("_|-");
        System.out.println(new Locale(ss[0]));

        ss = "zh_CN".split("_|-");
        System.out.println(new Locale(ss[0], ss[1]));

        ss = "zh-cn".split("_|-");
        System.out.println(new Locale(ss[0], ss[1]));

        ss = "zh_CN-HANS".split("_|-");
        System.out.println(new Locale(ss[0], ss[1], ss[2]));
    }
}
