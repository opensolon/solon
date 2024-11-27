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
package labs.case1;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;

/**
 * @author noear 2022/3/8 created
 */
public class UriTest {
    @Test
    @SuppressWarnings("ALL")
    public void test(){

        Assertions.assertThrows(NullPointerException.class, () -> URI.create(null));

        URI uri = URI.create("");
        Assertions.assertNotNull(uri);
        System.out.println(uri);
        System.out.println(uri.getHost());
        System.out.println(uri.getPath());

        assert uri.getHost() == null;
        assert "".equals(uri.getPath());
    }
}
