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

import cn.hutool.core.date.DateTime;
import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2021/5/3 created
 */
@SolonTest
public class IdTest {
    @Test
    public void time1(){
        //1420041600000
        DateTime dateTime = DateTime.of("2015-01-01 00:00:00","yyyy-MM-dd HH:mm:ss");
        System.out.println(dateTime.getTime());
    }

    @Test
    public void time2(){
        //1577808000000
        DateTime dateTime = DateTime.of("2020-01-01 00:00:00","yyyy-MM-dd HH:mm:ss");
        System.out.println(dateTime.getTime());
    }

    @Test
    public void time3(){
        //1893427200000
        DateTime dateTime = DateTime.of("2030-01-01 00:00:00","yyyy-MM-dd HH:mm:ss");
        System.out.println(dateTime.getTime());
    }

    @Test
    public void id() {
        //838528494260015104
        //177092937095925760
        System.out.println(CloudClient.id().generate());

        long start = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
            CloudClient.id().generate();
        }

        System.out.println("times: " + (System.currentTimeMillis() - start) + "ms");
    }
}
