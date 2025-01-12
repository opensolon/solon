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
package labs;

import org.noear.solon.core.wrap.MethodKey;

import java.lang.reflect.Method;

/**
 * @author noear 2024/6/24 created
 */
public class MethodTest {
    public static void main(String[] args) {
        Method main0 = MethodTest.class.getDeclaredMethods()[0];
        MethodKey methodKey = new MethodKey(main0, MethodTest.class);

        System.out.println(methodKey.hashCode());
        System.out.println(methodKey);

        MethodKey methodKey2 = new MethodKey(main0, MethodTest.class);
        System.out.println(methodKey2.hashCode());
        System.out.println(methodKey2);
    }
}
