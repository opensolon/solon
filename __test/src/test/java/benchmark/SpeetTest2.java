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
package benchmark;

import org.junit.jupiter.api.Test;
import org.noear.solon.web.staticfiles.StaticMimes;

public class SpeetTest2 {
    long time_start;
    long time_end;

    @Test
    public void test1() {

        String path1 = "/file.txt";
        String path2 = "/file.eot";


        System.out.println(StaticMimes.findByFileName(path1));
        time_start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            StaticMimes.findByFileName(path1);
        }
        time_end = System.currentTimeMillis();
        System.out.println("path1: " + (time_end - time_start));


        System.out.println(StaticMimes.findByFileName(path2));
        time_start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            StaticMimes.findByFileName(path2);
        }
        time_end = System.currentTimeMillis();
        System.out.println("path2: " + (time_end - time_start));
    }
}
