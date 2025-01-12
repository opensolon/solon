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
import org.noear.solon.test.HttpTester;

public class NginxHotUpdateTest extends HttpTester {
    @Test
    public void test1() throws Exception {
        new Thread(() -> {
            test1Do();
        }).start();

        System.in.read();
    }

    private void test1Do() {
        String url = "http://rock.sponge.io/getAppByID?appID=10970";

        while (true) {
            try {
                http(url).get();
                System.out.println(System.currentTimeMillis());
            } catch (Throwable ex) {
                ex.printStackTrace();
                break;
            }
        }
    }
}
