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
package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.server.prop.GzipProps;

/**
 * @author noear 2023/9/27 created
 */
public class MimeTest {
    @Test
    public void test(){
        assert "a".equals(GzipProps.resolveMime("a;b"));
        assert "a".equals(GzipProps.resolveMime("a"));
    }

    @Test
    public void test2_a(){
        long start = System.currentTimeMillis();
        for (int i=0; i< 1000000; i++){
            "a".split(";");
            "a;b".split(";");
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void test2_b(){
        long start = System.currentTimeMillis();
        for (int i=0; i< 1000000; i++){
            GzipProps.resolveMime("a");
            GzipProps.resolveMime("a;b");
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
