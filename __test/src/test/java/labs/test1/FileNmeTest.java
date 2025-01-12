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

import java.net.URLEncoder;

/**
 * @author noear 2021/8/4 created
 */
public class FileNmeTest {
    @Test
    public void test() throws Exception{
        String fileName = URLEncoder.encode("没有耳多 aaa.jar","utf-8");

        System.out.println(fileName);
        System.out.println(URLEncoder.encode(fileName,"utf-8"));
    }

    @Test
    public void test1(){
        System.out.println(System.getenv("file.encoding"));
        System.out.println(System.getProperty("file.encoding"));
    }
}
