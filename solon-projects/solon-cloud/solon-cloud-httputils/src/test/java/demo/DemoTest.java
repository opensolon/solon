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
package demo;

import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.utils.http.HttpUtils;

/**
 * @author noear 2021/10/13 created
 */
public class DemoTest {
    @Test
    public void demo0() throws Exception {
        //基于具体地址去调用
        //
        HttpUtils.http("http://h5.noear.org").get();
    }

    @Test
    public void demo1() throws Exception {
        //基于具体地址去调用
        //
        HttpUtils.http("http://x.x.x/demo").get();

        HttpUtils.http("http://x.x.x/demo").data("name", "noear").post();
    }

    @Test
    public void demo2() throws Exception {
        //基于负载均衡去调用
        //
        HttpUtils.http("userapi", "/api/demo").get();

        HttpUtils.http("userapi", "/api/demo").data("name", "noear").post();
    }

    @Test
    public void demo3() throws Exception {
        //基于具体地址去调用
        //
        HttpUtils.http("x.x.x/demo").get();

        HttpUtils.http("x.x.x/demo").data("name", "noear").post();

        HttpUtils.http("x.x.x/demo").data("name", "noear").multipart(true).post();
    }
}
