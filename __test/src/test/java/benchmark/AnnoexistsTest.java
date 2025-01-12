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

/**
 * @author noear 2021/5/18 created
 */
public class AnnoexistsTest {
    @Test
    public void test() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
            if (AnnoexistsTest.class.getAnnotations().length > 0) {

            }
        }
        System.out.println((System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test2() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {

        }
        System.out.println((System.currentTimeMillis() - start) + "ms");
    }
}
